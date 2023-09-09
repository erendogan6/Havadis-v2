package com.erendogan.havadis_v2.adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.erendogan.havadis_v2.model.PaylasimModel
import com.erendogan.havadis_v2.R
import com.erendogan.havadis_v2.databinding.RecyclerRowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class PaylasimAdaptor(
    private val paylasimlar : ArrayList<PaylasimModel>,
    private val context:Context) :
    RecyclerView.Adapter<PaylasimAdaptor.GorunumTutucu>() {

    class GorunumTutucu(val binding:RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    private var auth:FirebaseAuth = Firebase.auth
    private var firebase:FirebaseFirestore = Firebase.firestore
    private var user = auth.currentUser?.email
    private var tiklanabilirMi=true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GorunumTutucu {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GorunumTutucu(binding)
    }

    override fun getItemCount(): Int {
        return paylasimlar.size
    }

    var yorumTiklanma: ((id: String) -> Unit)? = null
    var disariTiklanma: (() -> Unit)? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: GorunumTutucu, position: Int) {

        // Mevcut paylaşımın bir değişken içerisine atanması
        val mevcutPaylasim: PaylasimModel = paylasimlar[position]

        // Alan bilgilerinin paylaşım bilgilerine göre doldurulması
        holder.binding.email.text = mevcutPaylasim.email
        holder.binding.aciklama.text = mevcutPaylasim.aciklama
        holder.binding.begeniSayisii.text = mevcutPaylasim.begeniSayisi
        holder.binding.tarih.text = mevcutPaylasim.tarih

        // Paylaşım türüne göre gerekli ayarlamaların yapılması
        if (mevcutPaylasim.paylasimTuru == "Metin") {
            holder.binding.imageView.visibility = View.GONE
        }else{
            Picasso.get().load(mevcutPaylasim.url).noFade().into(holder.binding.imageView)
        }

        // Profil resimlerinin ayarlanması
        firebase.collection("profil").document(paylasimlar[position].email).get().addOnSuccessListener {
            if (it.get("url")!=null) {
                val url = it.get("url") as String
                Picasso.get().load(url).noFade().into(holder.binding.profilKucukResim)
            }
        }



        //Yorum fonksiyonlarının ayarlanması
        holder.binding.yorumResim.setOnClickListener {
            yorumTiklanma?.invoke(paylasimlar[position].id)
        }
        holder.itemView.setOnClickListener {
            disariTiklanma?.invoke()
        }
        holder.binding.yorumSayisi.text= paylasimlar[position].yorumSayisi

        //Beğeni durumunun tespiti ve kırmızı-siyah kalp ayarlamasının yapılması
        val id = mevcutPaylasim.id
        val referans = firebase.collection("Paylasimm").document(id)
        var begeniDurum = false
        referans.get().addOnSuccessListener {
            if (it.exists()){
                val begenenler:List<String> = it.get("begenenler") as List<String>
                begeniDurum = begenenler.contains(user)
                holder.binding.imageView3.setImageResource(if (begeniDurum) R.drawable.redheart else R.drawable.blackheart)
            }
        }

        // Kullanıcının resmi beğenme fonksiyonunun ayarlanması
        holder.binding.imageView3.setOnClickListener {
            if(!tiklanabilirMi)
                return@setOnClickListener

                tiklanabilirMi=false
                holder.binding.imageView3.isClickable = false
                holder.binding.imageView3.isEnabled = false

                val begeniEkleKaldir = if (begeniDurum)  FieldValue.arrayRemove(user)  else FieldValue.arrayUnion(user)
                referans.update("begenenler",begeniEkleKaldir).addOnSuccessListener {

                val begeniDegisim = if (begeniDurum) -1 else 1
                referans.update("begeniSayisi", FieldValue.increment(begeniDegisim.toLong()))

                val begeniSayi = mevcutPaylasim.begeniSayisi.toInt() + begeniDegisim
                holder.binding.begeniSayisii.text = begeniSayi.toString()

                val begeniDurumuMetni = if (begeniDurum) "Beğeni Kaldırıldı" else "Beğenildi"
                toastGoster(begeniDurumuMetni)

                val heartImageResource = if (begeniDurum) R.drawable.blackheart else R.drawable.redheart
                holder.binding.imageView3.setImageResource(heartImageResource)
                }

                val handler = android.os.Handler(Looper.getMainLooper())
                handler.postDelayed({
                    tiklanabilirMi = true
                    holder.binding.imageView3.isClickable=true
                    holder.binding.imageView3.isEnabled = true
                }, 1500)
        }
    }
    private fun toastGoster(mesaj: String){
        Toast.makeText(context,mesaj,Toast.LENGTH_SHORT).show()
    }
}