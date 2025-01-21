package delivery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.btcmap.databinding.FragmentDeliveryBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import search.SearchResultModel

class DeliveryFragment : Fragment() {

    private val args = lazy {
        DeliveryModel.Args(
            userLat = requireArguments().getFloat("userLat").toDouble(),
            userLon = requireArguments().getFloat("userLon").toDouble(),
            searchAreaId = requireArguments().getLong("searchAreaId"),
        )
    }

    private val model: DeliveryModel by viewModel()

    private val resultModel: SearchResultModel by activityViewModel()

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!

    private val adapter = DeliveryAdapter { item ->
        resultModel.element.update { item.element }
        parentFragmentManager.popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                model.items.collect { adapter.submitList(it) }
            }
        }

        Log.d("DeliveryFragment", "args: ${args.value}")
        model.setArgs(args.value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}