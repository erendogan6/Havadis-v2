package com.erendogan.havadis_v2.fragment

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.erendogan.havadis_v2.R
import com.erendogan.havadis_v2.databinding.FragmentProfilBinding
import com.erendogan.havadis_v2.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfilEkrani : Fragment() {

    private lateinit var binding: FragmentProfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var kullanici: FirebaseUser

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentProfilBinding.inflate(inflater, container, false)
        val animationDrawable = binding.profilEkraniArkaplan.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(3000)
        animationDrawable.setExitFadeDuration(3000)
        animationDrawable.start()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        kullanici = auth.currentUser!!

        firestore = Firebase.firestore
        binding.profilMail.text = auth.currentUser?.email.toString()

        firestore.collection("profil").document(kullanici.email.toString()).get().addOnSuccessListener {
            if (it.get("url")!=null) {
                val url = it.get("url") as String
                Picasso.get().load(url).noFade().into(binding.profilResim)
            }
        }


        binding.profilResimLayout.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_activity_navigasyon).navigate(R.id.action_navigation_profil_to_profilResim2)
        }
        binding.cikisYapLayout.setOnClickListener {
            cikis()
        }
        binding.sifreDegistirLayout.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_activity_navigasyon).navigate(R.id.action_navigation_profil_to_sifreDegistir)
        }
    }

    private fun cikis() {
        auth.signOut()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}