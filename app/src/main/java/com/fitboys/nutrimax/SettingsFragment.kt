import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.fitboys.nutrimax.LoginActivity
import com.fitboys.nutrimax.NotificationActivity
import com.fitboys.nutrimax.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment(R.layout.fragment_settings){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNotifications = view.findViewById<Button>(R.id.btnNotifications);
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        btnNotifications.setOnClickListener(View.OnClickListener {view: View? ->
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        })



        btnLogout.setOnClickListener(View.OnClickListener {view: View? ->
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        })



    }

}