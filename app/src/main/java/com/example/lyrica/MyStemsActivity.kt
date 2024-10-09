import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lyrica.R
import com.google.firebase.firestore.FirebaseFirestore

class MyStemsActivity : AppCompatActivity() {
    private lateinit var stemsTextView: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_stem_activity_main)

        stemsTextView = findViewById(R.id.stemsTextView)
        //db = FirebaseFires
        //tore.getInstance()

        loadStemsFromFirebase()
    }

    private fun loadStemsFromFirebase() {
        db.collection("stems").get()
            .addOnSuccessListener { result ->
                val builder = StringBuilder()
                for (document in result) {
                    val vocalsUrl = document.getString("vocalsUrl")
                    val instrumentalsUrl = document.getString("instrumentalsUrl")
                    builder.append("Vocals: $vocalsUrl\nInstrumentals: $instrumentalsUrl\n\n")
                }
                stemsTextView.text = builder.toString()

                // Set click listeners for URLs
                stemsTextView.setOnClickListener {
                }
            }
            .addOnFailureListener { e ->
                stemsTextView.text = "Error loading stems: $e"
            }
    }

    private fun openLink(url: String?) {
        if (url != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}
