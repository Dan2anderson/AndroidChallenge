package com.owletcare.androidtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.owletcare.androidtest.redux.Action
import com.owletcare.androidtest.redux.Store
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val appReducer: (Action, State) -> State = { action, state ->
        when (action) {
            is UsersAction -> {
                State(action.reduce(state.users))
            }
            else -> state
        }
    }

    private val store = Store(appReducer, State(arrayListOf()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        addUserButton.setOnClickListener {
            /**
             * By default data class's compare by structure.  Given that in this program users are generated with a random combination of
             * 5 names and 5 images,  it is practically guaranteed that we will get users who are identical when compared by structure.
             * originally I though I would override User.equals() to check equality by reference, but then I was not sure how to override HashCode
             * in such a way that it would unique if between to User instance with the same data.
             *
             * So I settled on adding a randomId up to 10,000.  This should make sure all User instances are unique.
             *
             * This solves a bug were occasionally (1 or 2 out of 10 )  when the add (or delete) user button was clicked no user was added or deleted.
             */
            store.dispatch(UsersAction.AddUser(randomName, randomProfilePicture, randomId))
        }
        mainRecyclerView.adapter = UserRecyclerAdapter(store)
    }

    private val randomName: String
        get() = when (Random.nextInt(5)) {
            0 -> "Jake"
            1 -> "Sally"
            2 -> "Mike"
            3 -> "Brian"
            else -> "Brower"
        }

    private val randomProfilePicture: String
        get() = when (Random.nextInt(5)) {
            0 -> "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
            1 -> "https://aboughina.files.wordpress.com/2015/12/portrait_photography_0131440.jpg"
            2 -> "https://cdnb.artstation.com/p/assets/images/images/001/863/575/large/irakli-nadar-artstation-da.jpg?1453903033"
            3 -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"
            else -> "https://vignette.wikia.nocookie.net/jamescameronsavatar/images/e/e6/Humansully.jpg/revision/latest?cb=20140829010952"
        }

    private val randomId: Int
        get() = Random.nextInt(10000)
}
