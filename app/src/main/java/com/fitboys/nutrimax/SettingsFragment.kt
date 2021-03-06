import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        val badge_settings = view.findViewById<TextView>(R.id.badge_settings);
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val bundle = arguments
        val counter = bundle!!.getString("notificationsCount")
        if (counter != null) {
            if(counter.toInt() != 0) {
                var nots=" $counter "
                badge_settings.text = nots
                badge_settings.visibility = View.VISIBLE;
            }
        }

        btnNotifications.setOnClickListener(View.OnClickListener {view: View? ->
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        })



        btnLogout.setOnClickListener(View.OnClickListener {view: View? ->
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        })



    }

}