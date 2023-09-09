package com.erendogan.havadis_v2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan.havadis_v2.R
import com.erendogan.havadis_v2.adaptor.PaylasimAdaptor
import com.erendogan.havadis_v2.databinding.FragmentZamanTuneliBinding
import com.erendogan.havadis_v2.model.PaylasimModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class ZamanTuneli : Fragment() {

    private var _binding: FragmentZamanTuneliBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adaptor: PaylasimAdaptor
    private lateinit var paylasimlar : ArrayList<PaylasimModel>
    private lateinit var listenerRegistration: ListenerRegistration


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentZamanTuneliBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= Firebase.auth

        firestore= Firebase.firestore

        paylasimlar = ArrayList()
        veriAl()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adaptor = PaylasimAdaptor(paylasimlar, requireActivity())
        binding.recyclerView.adapter = adaptor
        adaptor.yorumTiklanma = { id ->
            val yeniFragment = YorumFragment(id)
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.yukari,0)
            fragmentTransaction.replace(R.id.yorumFragment, yeniFragment, "Yorum")
            fragmentTransaction.addToBackStack("Yorum")
            fragmentTransaction.commit()
        }

        adaptor.disariTiklanma = {
            val fragment = requireActivity().supportFragmentManager.findFragmentByTag("Yorum")
            if (fragment != null && fragment.isVisible) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(0, R.anim.asagi) // Kapanış animasyonu
                transaction.remove(fragment)
                transaction.commit()
            }
        }
    }


    private fun toastGoster(mesaj:String){
        Toast.makeText(requireActivity(),mesaj, Toast.LENGTH_SHORT).show()
    }

    private fun veriAl(){
        listenerRegistration = firestore.collection("Paylasimm").orderBy("tarih",
            Query.Direction.DESCENDING).addSnapshotListener { veri, hata ->
            if (hata!=null){
                hata.localizedMessage?.let { toastGoster(it) }
            }
            else{
                if (veri!=null)
                    if (!veri.isEmpty){
                        val dokumanlar = veri.documents
                        paylasimlar.clear()
                        for (dokuman in dokumanlar){
                            val id = dokuman.id
                            val aciklama = dokuman.get("aciklama") as String
                            val email = dokuman.get("email") as String
                            val url = dokuman.get("url").let { it.toString() }
                            val begeniSayisi = dokuman.get("begeniSayisi").toString()
                            val yorumSayisi: String = if (dokuman.get("yorumlar")!=null) {
                                val yorumlar = dokuman.get("yorumlar") as List<Map<String, String>>?
                                yorumlar?.size.toString()
                            } else{
                                "0"
                            }
                            val paylasimTuru =dokuman.get("paylasimTuru") as String
                            val tarih = dokuman.get("tarih") as Timestamp

                            val tarihFormatli = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(tarih.toDate())

                            paylasimlar.add(PaylasimModel(id,email,aciklama,url,begeniSayisi,paylasimTuru,tarihFormatli,yorumSayisi))
                        }
                        adaptor.notifyDataSetChanged()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listenerRegistration.remove()
    }
}