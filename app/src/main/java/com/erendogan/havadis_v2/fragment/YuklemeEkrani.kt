package com.erendogan.havadis_v2.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.erendogan.havadis_v2.R
import com.erendogan.havadis_v2.databinding.FragmentYuklemeEkraniBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class YuklemeEkrani : Fragment() {

    private var _binding: FragmentYuklemeEkraniBinding? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var secilenResim: Uri? = null
    private lateinit var kullanici: FirebaseUser
    private var islem:Boolean=false
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentYuklemeEkraniBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLauncher()
        auth= Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
        kullanici = auth.currentUser!!

        val animationDrawable = binding.upload.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(3000)
        animationDrawable.setExitFadeDuration(3000)
        animationDrawable.start()
        binding.imageView.setOnClickListener {
            yukle(it)
        }
        binding.yuklemeButonu.setOnClickListener {
            yuklemeButonu()
        }
    }

    private fun yuklemeButonu() {
        if (islem) {
            return
        }
        islem = true

        val aciklama = binding.aciklama.text.toString()
        if (aciklama.isBlank()) {
            toastGoster("Açıklama Giriniz")
            islem = false
            return
        }

        val referans = storage.reference
        val isim = UUID.randomUUID().toString() + ".jpg"
        val resimReferans = referans.child("resimler/$isim")

        val paylasimVeri = hashMapOf(
            "email" to kullanici.email!!,
            "aciklama" to aciklama,
            "tarih" to Timestamp.now(),
            "begeniSayisi" to 0,
            "begenenler" to listOf("null"),
        )

        secilenResim?.let { resim ->
            resimReferans.putFile(resim)
                .addOnSuccessListener {
                    val yuklenenResimReferans = storage.reference.child("resimler").child(isim)
                    yuklenenResimReferans.downloadUrl.addOnSuccessListener { url ->
                        paylasimVeri["url"] = url.toString()
                        paylasimVeri["paylasimTuru"] = "Gorsel"
                        ekleVeGeriDon(paylasimVeri)
                    }
                }
                .addOnFailureListener { hata ->
                    hata.localizedMessage?.let { toastGoster(it) }
                }
        } ?: run {
            paylasimVeri["paylasimTuru"] = "Metin"
            ekleVeGeriDon(paylasimVeri)
        }
    }

    private fun ekleVeGeriDon(veri: HashMap<String, Any>) {
        firestore.collection("Paylasimm").add(veri)
            .addOnSuccessListener {
                requireActivity().findNavController(R.id.nav_host_fragment_activity_navigasyon).navigate(R.id.action_navigation_paylasim_to_navigation_zamanTuneli)
            }
            .addOnFailureListener { hata ->
                hata.localizedMessage?.let { toastGoster(it) }
            }
    }

    private fun yukle(view: View){
        val izin = if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            "android.permission.READ_MEDIA_IMAGES"
        }
        else{
            "android.permission.READ_EXTERNAL_STORAGE"
        }

        if (ContextCompat.checkSelfPermission(requireActivity(),izin)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),izin)){
                Snackbar.make(view,"Galeri için izin gereklidir.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin Ver") {
                        // izin istencek
                        permissionLauncher.launch(izin)
                    }.show()
            }
            else {
                // izin istencek
                permissionLauncher.launch(izin)
            }
        }
        else {
            // galeriye gidilecek
            val intentGaleri = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentGaleri)
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val intentSonuc = it.data
                if (intentSonuc != null) {
                    secilenResim = intentSonuc.data
                    secilenResim.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
            if (it) {
                val intentGaleri =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGaleri)
            } else {
                toastGoster("İzin Gerekli")
            }
        }
    }

    private fun toastGoster(mesaj: String ){
        Toast.makeText(requireActivity(), mesaj, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}