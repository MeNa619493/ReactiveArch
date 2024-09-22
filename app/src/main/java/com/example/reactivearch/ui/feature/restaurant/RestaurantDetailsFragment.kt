package com.example.reactivearch.ui.feature.restaurant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reactivearch.databinding.FragmentRestaurantDetailsBinding
import com.example.reactivearch.domain.entity.Restaurant
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_RESTAURANT= "restaurant"

@AndroidEntryPoint
class RestaurantDetailsFragment : Fragment() {

    private var restaurant: Restaurant? = null
    private var _binding : FragmentRestaurantDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getParcelable<Restaurant>(ARG_RESTAURANT).also { restaurant = it }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentRestaurantDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            restaurant?.address?.let {
                if (it.isEmpty())
                    address.text = ADDRESS.plus(NA)
                else
                    address.text = ADDRESS.plus(it)
            }

            restaurant?.name?.let {
                name.text = NAME.plus(it)
            }

            restaurant?.city?.let {
                if (it.isEmpty())
                    city.text = CITY.plus(NA)
                else
                    city.text = CITY.plus(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding =null
    }

    companion object {
        @JvmStatic
        fun newInstance(restaurant: Restaurant?) =
            RestaurantDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RESTAURANT,restaurant)
                }
            }

        const val  ADDRESS = "Address: "
        const val CITY = "City: "
        const val  NAME = "Name: "
        const val NA = "N/A"
    }
}