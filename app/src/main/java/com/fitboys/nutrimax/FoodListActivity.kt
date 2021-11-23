package com.fitboys.nutrimax

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import android.os.Bundle
import com.fitboys.nutrimax.R
import com.google.android.gms.tasks.OnSuccessListener
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import com.google.android.gms.tasks.OnFailureListener
import java.io.File
import java.io.IOException

class FoodListActivity : AppCompatActivity() {
    private val mStoreReference = FirebaseStorage.getInstance().reference
        .child("images/428cb52d-f4dd-414f-b731-2f1c21596388.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)
        try {
            val localFile = File.createTempFile("food", "jpg")
            mStoreReference.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    (findViewById<View>(R.id.image_view) as ImageView).setImageBitmap(bitmap)
                }.addOnFailureListener { }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}