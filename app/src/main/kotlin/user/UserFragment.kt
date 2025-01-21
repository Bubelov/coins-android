package user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import element.ElementFragment
import event.EventsAdapter
import event.EventsRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.btcmap.R
import org.btcmap.databinding.FragmentUserBinding
import org.koin.android.ext.android.inject

class UserFragment : Fragment() {

    private data class Args(
        val userId: Long,
    )

    private val args = lazy {
        Args(requireArguments().getLong("user_id"))
    }

    private val usersRepo: UsersRepo by inject()

    private val eventsRepo: EventsRepo by inject()

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val adapter = EventsAdapter(object : EventsAdapter.Listener {
        override fun onItemClick(item: EventsAdapter.Item) {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<ElementFragment>(
                    R.id.nav_host_fragment, null, bundleOf("element_id" to item.elementId)
                )
                addToBackStack(null)
            }
        }

        override fun onShowMoreClick() {}
    }).apply {
        canLoadMore = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val user = runBlocking {
            usersRepo.selectById(args.value.userId)
        } ?: return

        val userName = user.osmData.optString("display_name")

        binding.topAppBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_view_on_osm) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.openstreetmap.org/user/${userName}")
                startActivity(intent)
            }

            true
        }

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                binding.topAppBar.title = userName.ifBlank { getString(R.string.unnamed_user) }

                val items = eventsRepo.selectByUserIdAsListItems(
                    requireArguments().getLong("user_id"),
                ).map {
                    EventsAdapter.Item(
                        date = it.eventDate,
                        type = it.eventType,
                        elementId = it.elementId,
                        elementName = it.elementName.ifBlank { getString(R.string.unnamed) },
                        username = "",
                        tipLnurl = "",
                    )
                }
                adapter.submitList(items)
                binding.topAppBar.subtitle = resources.getQuantityString(
                    R.plurals.d_changes,
                    items.size,
                    items.size,
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}