package com.erendogan.havadis_v2.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erendogan.havadis_v2.databinding.YorumRowBinding
import com.erendogan.havadis_v2.model.YorumModel

class YorumAdaptor(private val yorumlar : ArrayList<YorumModel> ) : RecyclerView.Adapter<YorumAdaptor.Holder>() {

    class Holder(val binding:YorumRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = YorumRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.yorumText.text = yorumlar[position].yorum
        holder.binding.yorumcuText.text = yorumlar[position].yorumcu
    }

    override fun getItemCount(): Int {
        return yorumlar.size
    }

}