import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fitboys.nutrimax.AddFoodActivity
import com.fitboys.nutrimax.FoodListActivity
import com.fitboys.nutrimax.MealRecordActivity
import com.fitboys.nutrimax.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment:Fragment(R.layout.fragment_home_fragment) {
    private var mAuth: FirebaseAuth? = null
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calories = view.findViewById<TextView>(R.id.calories);
        val recordFood = view.findViewById<Button>(R.id.recordFood)
        val addFood = view.findViewById<Button>(R.id.addFood)
        val seeRecord = view.findViewById<ImageView>(R.id.fireImage)
        seeRecord.animate().rotationBy(360F).duration = 1200

        mAuth = FirebaseAuth.getInstance()
        mAuth?.currentUser?.uid?.let {
            db.collection("users")
                .document(it).get()
                .addOnSuccessListener { document ->
                    Log.d(TAG, "Read document with ID ${document.id}")
                    if (calories != null) {
                        calories.text = document.data?.get("remainingCalories").toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents $exception")
                }
            recordFood.setOnClickListener { view ->
                startActivity(Intent(requireContext(), FoodListActivity::class.java))
            }

            addFood.setOnClickListener { view ->
                startActivity(Intent(requireContext(), AddFoodActivity::class.java))
            }
            seeRecord.setOnClickListener{ view ->
                startActivity(Intent(requireContext(), MealRecordActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()


    }

}