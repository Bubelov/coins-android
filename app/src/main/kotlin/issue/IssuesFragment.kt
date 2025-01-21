package issue

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.btcmap.databinding.FragmentIssuesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class IssuesFragment : Fragment() {

    private val args = lazy {
        IssuesModel.Args(requireArguments().getLong("area_id"))
    }

    private val model: IssuesModel by viewModel()

    private var _binding: FragmentIssuesBinding? = null
    private val binding get() = _binding!!

    private val adapter = IssuesAdapter(object : IssuesAdapter.Listener {
        override fun onItemClick(item: IssuesAdapter.Item) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(item.osmUrl)
            startActivity(intent)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentIssuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        model.setArgs(args.value)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                model.state.collect {
                    when (it) {
                        is IssuesModel.State.ShowingItems -> {
                            adapter.submitList(it.items)
                        }

                        else -> {}
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}