package com.erendogan.havadis_v2.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan.havadis_v2.adaptor.YorumAdaptor
import com.erendogan.havadis_v2.databinding.FragmentYorumBinding
import com.erendogan.havadis_v2.model.YorumModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class YorumFragment(private val postID:String) : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var _binding: FragmentYorumBinding
    private lateinit var yorumlar: ArrayList<YorumModel>
    private lateinit var adaptor: YorumAdaptor

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentYorumBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.yorumGonderButon.setOnClickListener { yorumYap() }
        auth= Firebase.auth
        firestore=Firebase.firestore

        yorumlar = ArrayList()

        veriAl()

        _binding.yorumRecycler.layoutManager = LinearLayoutManager(requireActivity())
        adaptor = YorumAdaptor(yorumlar)
        _binding.yorumRecycler.adapter = adaptor
    }

    private fun veriAl(){
        firestore.collection("Paylasimm")
            .document(postID)
            .addSnapshotListener { document, error ->
                if (document != null) {
                    val comments = document["yorumlar"] as List<Map<String, String>>?
                    if (comments != null) {
                        yorumlar.clear()
                        for (comment in comments) {
                            val yorum = comment["yorum"] as String
                            val yorumcu = comment["yorumcu"] as String
                            yorumlar.add(YorumModel(yorum,yorumcu))
                        }
                        adaptor.notifyDataSetChanged()
                    }
                }
                else{
                    if (error != null) {
                        error.localizedMessage?.let { toastGoster(it) }
                    }
                }
            }
    }

    private fun yorumYap() {

        if (_binding.mailInputt.text.toString().isBlank()){
            toastGoster("Lütfen Yorum Giriniz")
            return
        }

        val yeniYorum = hashMapOf(
            "yorum" to _binding.mailInputt.text.toString(),
            "yorumcu" to auth.currentUser!!.email
        )

        firestore.collection("Paylasimm")
            .document(postID)
            .update("yorumlar", FieldValue.arrayUnion(yeniYorum))
            .addOnSuccessListener {
                toastGoster("Yorum Eklendi")
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { toastGoster(it) }
            }
    }

    private fun toastGoster (mesaj:String){
        Toast.makeText(requireView().context, mesaj, Toast.LENGTH_SHORT).show()
    }
}