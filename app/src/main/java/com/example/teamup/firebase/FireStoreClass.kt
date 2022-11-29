package com.example.teamup.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.teamup.activities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.example.teamup.models.*
import com.example.teamup.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisterSuccess()
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error Writing Document",
                    e
                )
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board Created Successfully")

                Toast.makeText(activity,
                "Board Created Successfully", Toast.LENGTH_SHORT).show()

                activity.boardCreatedSuccessFully()
            }
            .addOnFailureListener{ e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error Creating Board",
                    e
                )
            }
    }

    fun getBoardList(activity: MainActivity) {
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                    Log.i(activity.javaClass.simpleName, document.documents.toString())
                    val boardList: ArrayList<Board> = ArrayList()
                    for(i in document.documents) {
                        val board = i.toObject(Board::class.java)!!
                        board.documentID = i.id
                        boardList.add(board)
                    }

                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener(){ e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error While Creating Board")
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser!=null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).
        document(getCurrentUserId()).
        update(userHashMap).
        addOnSuccessListener {
            Log.e(activity.javaClass.simpleName, "Profile Data Updated")
            Toast.makeText(activity, "Profile Updated", Toast.LENGTH_LONG).show()
            activity.profileUpdateSuccess()
        }.addOnFailureListener{
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Update Error", e)
        }
    }

    fun loadUserData(activity: Activity, readBoardList: Boolean = false) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get().addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val loggedInUser: User? = document.toObject(User::class.java)
                when(activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }.addOnFailureListener {
                e->
                    when(activity) {
                        is SignInActivity -> {
                            activity.hideProgressDialog()
                        }
                        is MainActivity -> {
                            activity.hideProgressDialog()
                        }
                        is MyProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                Log.e(activity.javaClass.simpleName, "Error Updating", e)
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentID = document.id
                activity.boardDetails(board)
            }
            .addOnFailureListener(){ e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error While Creating Board")
            }
    }

    fun addUpdateTaskList(activity: TaskListActivity, board: Board) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Task List Successfully")
                activity.addUpdateTaskListSuccess()
            }
            .addOnFailureListener{
                exception->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error While Creating Board", exception)
            }
    }
}