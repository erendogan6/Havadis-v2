package com.erendogan.havadis_v2.view

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erendogan.havadis_v2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = Firebase.auth
        val kullanici = auth.currentUser
        if (kullanici!=null){
            val intent = Intent(this, Navigasyon::class.java)
            startActivity(intent)
            finish()
        }

        val animationDrawable = binding.linear.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(3000)
        animationDrawable.setExitFadeDuration(3000)
        animationDrawable.start()
    }

    private fun toastGoster(mesaj:String){
        Toast.makeText(this,mesaj,Toast.LENGTH_SHORT).show()
    }

    fun kayitOl(view: View){
        val email = binding.mailInputt.text.toString()
        val sifre = binding.passwordInputt.text.toString()

        if (email.isBlank() || sifre.isBlank()){
            toastGoster("E-Posta Veya Şifre Boş Olamaz !")
            return
        }

        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener {
            if (it.isSuccessful){
                toastGoster("Kayıt Başarılı")
                val intent = Intent(this, Navigasyon::class.java)
                startActivity(intent)
                finish()
            }

        }.addOnFailureListener {
            it.localizedMessage?.let { it1 -> toastGoster(it1) }
        }

    }

    fun girisYap(view:View){
        val email = binding.mailInputt.text.toString()
        val sifre = binding.passwordInputt.text.toString()

        if (email.isBlank() || sifre.isBlank()){
            toastGoster("E-Posta Veya Şifre Boş Olamaz !")
            return
        }

        auth.signInWithEmailAndPassword(email,sifre).addOnCompleteListener {
            if (it.isSuccessful){
                toastGoster("Giriş Başarılı")
                val intent = Intent(this, Navigasyon::class.java)
                startActivity(intent)
                finish()
            }

        }.addOnFailureListener {
            it.localizedMessage?.let { it1 -> toastGoster(it1) }
        }
    }

    fun sifirla(view:View){
        val email = binding.mailInputt.text.toString()

        if (email.isBlank()){
            toastGoster("E-Posta Veya Şifre Boş Olamaz !")
            return
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful){
                toastGoster("Şifre Sıfırlama Mail'i Gönderildi")
            }
            else{
                it.exception?.localizedMessage?.let { it1 -> toastGoster(it1) }
            }
        }
    }
}