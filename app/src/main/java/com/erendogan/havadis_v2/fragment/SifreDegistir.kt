package com.erendogan.havadis_v2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.erendogan.havadis_v2.R
import com.erendogan.havadis_v2.databinding.FragmentSifreDegistirBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SifreDegistir : Fragment() {

    private lateinit var binding: FragmentSifreDegistirBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var kullanici:FirebaseUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentSifreDegistirBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        kullanici = auth.currentUser!!
        binding.sifreDegistirButon.setOnClickListener {
            sifreDegistir()
        }
        binding.constraintLayout4.setOnClickListener {
            back()
        }
    }
    private fun sifreDegistir(){
        if (binding.eskiSifre.text.toString().isBlank()){
            toastGoster("Eski Şifrenizi Giriniz")
            return
        }

        if (binding.yeniSifre.text.toString().isBlank() || binding.yeniSifre2.text.toString().isBlank()){
            toastGoster("Yeni Şifrenizi Giriniz")
            return
        }

        if (binding.yeniSifre.text.toString()!=binding.yeniSifre2.text.toString()){
            toastGoster("Yeni Şifrenizi Hatalı Girdiniz")
            return
        }

        val eskiSifre = binding.eskiSifre.text.toString()
        val yeniSifre = binding.yeniSifre2.text.toString()

        val kimlik = EmailAuthProvider.getCredential(kullanici.email!!,eskiSifre)

        kullanici.reauthenticate(kimlik).addOnSuccessListener {
            kullanici.updatePassword(yeniSifre).addOnSuccessListener {
                toastGoster("Şifre Değiştirildi")
                requireActivity().findNavController(R.id.nav_host_fragment_activity_navigasyon).navigate(R.id.action_sifreDegistir_to_navigation_profil)
            }.addOnFailureListener {
                toastGoster("Hata Oluştu")
            }
        }.addOnFailureListener {
            toastGoster("Eski Şifrenizi Yanlış Girdiniz")
            it.localizedMessage?.let { it1 -> toastGoster(it1) }
        }

    }

    private fun back(){
        requireActivity().findNavController(R.id.nav_host_fragment_activity_navigasyon).navigate(R.id.action_sifreDegistir_to_navigation_profil)
    }

    private fun toastGoster(mesaj : String){
        Toast.makeText(requireActivity(),mesaj, Toast.LENGTH_SHORT).show()
    }
}