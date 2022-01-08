import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.app.AlertDialog
import android.content.Intent
import android.text.TextUtils.replace
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import com.fitboys.nutrimax.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ProfileFragment : Fragment(R.layout.fragment_profile) {
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

        val target_calories = view.findViewById<TextView>(R.id.target_calories);
        var remainingCalories : String = "0"
        var oldCalories : String = "0"

        val tvProfileWeight = view.findViewById<TextView>(R.id.tvProfileWeight)
        val tvProfileHeight = view.findViewById<TextView>(R.id.tvProfileHeight)
        val constraint1 = view.findViewById<ConstraintLayout>(R.id.constraint1)
        val constraint2 = view.findViewById<ConstraintLayout>(R.id.constraint2)
        val constraint3 = view.findViewById<ConstraintLayout>(R.id.constraint3)

        mAuth = FirebaseAuth.getInstance()
        mAuth?.currentUser?.uid?.let {
            db.collection("users")
                .document(it).get()
                .addOnSuccessListener { document ->

                    Log.d(ContentValues.TAG, "Read document with ID ${document.id}")
                    target_calories.text = document.data?.get("caloriesIntake").toString()
                    tvProfileWeight.text = document.data?.get("weight").toString()
                    tvProfileHeight.text = document.data?.get("height").toString()
                    remainingCalories = document.data?.get("remainingCalories").toString()
                    oldCalories = document.data?.get("caloriesIntake").toString()
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents $exception")
                }
            constraint1.setOnClickListener(View.OnClickListener { view: View? ->

                val layoutInflater = LayoutInflater.from(requireContext())
                val popupInputDialogView: View =
                    layoutInflater.inflate(R.layout.popup_input_dialog_calories, null)

                val saveButton = popupInputDialogView.findViewById<Button>(R.id.button_save);
                val calculatorButton = popupInputDialogView.findViewById<Button>(R.id.button_calculator);
                val cancelButton = popupInputDialogView.findViewById<Button>(R.id.button_cancel);

                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Update calories intake")
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_update_24)
                alertDialogBuilder.setCancelable(false)


                alertDialogBuilder.setView(popupInputDialogView)

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()

                saveButton.setOnClickListener(View.OnClickListener {
                    val newCalories = popupInputDialogView.findViewById<EditText>(R.id.calories);
                    val newCaloriesIntake = newCalories.text.toString()
                    val diff = newCaloriesIntake.toInt() - oldCalories.toInt()
                    Log.d(ContentValues.TAG, "Calories diff: $diff")
                    val db = Firebase.firestore
                    mAuth?.currentUser?.uid?.let { it ->
                        db.collection("users")
                            .document(it).update(mapOf(
                                "caloriesIntake" to newCaloriesIntake,
                                "remainingCalories" to (remainingCalories.toInt() + diff).toString())
                            )
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "DocumentSnapshot updated")
                                startActivity(Intent(requireContext(), MyProfileActivity::class.java))
                                }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating document", e)
                            }

                    }
                })

                calculatorButton.setOnClickListener(View.OnClickListener {
                    startActivity(Intent(requireContext(), CaloriesCalculatorActivity::class.java))
                })

                cancelButton.setOnClickListener(View.OnClickListener { alertDialog.cancel() })
            })

            constraint2.setOnClickListener(View.OnClickListener { view: View? ->

                val layoutInflater = LayoutInflater.from(requireContext())
                val popupInputDialogView: View =
                    layoutInflater.inflate(R.layout.popup_input_dialog_weight, null)

                val saveButton = popupInputDialogView.findViewById<Button>(R.id.button_save);
                val cancelButton = popupInputDialogView.findViewById<Button>(R.id.button_cancel);

                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Update weight")
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_update_24)
                alertDialogBuilder.setCancelable(false)


                alertDialogBuilder.setView(popupInputDialogView)

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()

                saveButton.setOnClickListener(View.OnClickListener {
                    val newWeight = popupInputDialogView.findViewById<EditText>(R.id.weight);
                    val newWeightNumber = newWeight.text.toString()
                    Log.d(ContentValues.TAG, "Weight: $newWeightNumber")
                    val db = Firebase.firestore
                    mAuth?.currentUser?.uid?.let { it ->
                        db.collection("users")
                            .document(it).update("weight", newWeightNumber)
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "DocumentSnapshot updated")
                                startActivity(Intent(requireContext(), MyProfileActivity::class.java))
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating document", e)
                            }

                    }
                })

                cancelButton.setOnClickListener(View.OnClickListener { alertDialog.cancel() })
            })

            constraint3.setOnClickListener(View.OnClickListener { view: View? ->

                val layoutInflater = LayoutInflater.from(requireContext())
                val popupInputDialogView: View =
                    layoutInflater.inflate(R.layout.popup_input_dialog_height, null)

                val saveButton = popupInputDialogView.findViewById<Button>(R.id.button_save);
                val cancelButton = popupInputDialogView.findViewById<Button>(R.id.button_cancel);

                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Update height")
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_update_24)
                alertDialogBuilder.setCancelable(false)


                alertDialogBuilder.setView(popupInputDialogView)

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()

                saveButton.setOnClickListener(View.OnClickListener {
                    val newHeight = popupInputDialogView.findViewById<EditText>(R.id.height);
                    val newHeightNumber = newHeight.text.toString()
                    Log.d(ContentValues.TAG, "Height: $newHeightNumber")
                    val db = Firebase.firestore
                    mAuth?.currentUser?.uid?.let { it ->
                        db.collection("users")
                            .document(it).update("height", newHeightNumber)
                            .addOnSuccessListener {
                                Log.d(ContentValues.TAG, "DocumentSnapshot updated")
                                startActivity(Intent(requireContext(), MyProfileActivity::class.java))
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error updating document", e)
                            }

                    }
                })

                cancelButton.setOnClickListener(View.OnClickListener { alertDialog.cancel() })
            })


        }

    }
}