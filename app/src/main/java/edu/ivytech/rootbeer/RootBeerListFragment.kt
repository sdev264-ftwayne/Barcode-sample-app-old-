package edu.ivytech.rootbeer

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ivytech.rootbeer.database.RootBeer
import edu.ivytech.rootbeer.databinding.ListItemBinding
import edu.ivytech.rootbeer.databinding.FragmentRootBeerListBinding
import java.io.File
import java.util.*


private const val TAG = "ListFragment"
class RootBeerListFragment : Fragment() {



    private var _binding: FragmentRootBeerListBinding? = null
    private val binding get() = _binding!!
    private var adapter:RootAdapter? = null
    private val listViewModel : RootBeerListViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootBeerListBinding  .inflate(inflater, container, false)
        val view = binding.root
        binding.rootbeerRecyclerView.layoutManager = GridLayoutManager(context,2)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addRbBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_List_to_Detail)
        }
        listViewModel.rootBeerListLiveData.observe(viewLifecycleOwner) { list ->
            updateUI(list)
        }
    }



    private fun updateUI(rootBeers:List<RootBeer>) {
        adapter = RootAdapter(rootBeers)
        binding.rootbeerRecyclerView.adapter = adapter
    }

    private inner class RootHolder(itemBinding: ListItemBinding) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        private lateinit var rootBeer: RootBeer
        val name : TextView = itemBinding.nameTextView
        val manufacturer : TextView = itemBinding.manufactTextView
        val image : ImageView = itemBinding.rootBeerImage
        val ratingBar : RatingBar = itemBinding.rootBeerRating
        init {
            itemBinding.root.setOnClickListener(this)
        }
        fun bind(rootBeer: RootBeer) {
            this.rootBeer = rootBeer
            name.text = rootBeer.name
            manufacturer.text = rootBeer.manufacturer

            val filesDir = requireActivity().applicationContext.filesDir
            val photoFile = File(filesDir, rootBeer.photoFileName)
            if (photoFile.exists()) {
                val bitmap: Bitmap? = getScaledBitmap(
                        photoFile.path,
                        requireActivity()
                )
                image.setImageBitmap(bitmap)
            }
            ratingBar.rating = rootBeer.rating

        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${rootBeer.name} pressed", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putSerializable(RootBeerDetailFragment.ARG_ITEM_ID, rootBeer.id)
            itemView.findNavController().navigate(R.id.action_List_to_Detail, bundle)
        }
    }

    private inner class RootAdapter(var rootBeers:List<RootBeer>) : RecyclerView.Adapter<RootHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RootHolder {
            val itemBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return RootHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: RootHolder, position: Int) {
            val rootBeer = rootBeers[position                                                                                  ]
            holder.bind(rootBeer)
        }

        override fun getItemCount(): Int {
            return rootBeers.size
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}