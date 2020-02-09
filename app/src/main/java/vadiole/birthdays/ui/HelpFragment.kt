package vadiole.birthdays.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.help_fragment.*
import vadiole.birthdays.BuildConfig
import vadiole.birthdays.HelpViewModel
import vadiole.birthdays.R
import vadiole.birthdays.utils.Event
import vadiole.birthdays.utils.LogUtil


class HelpFragment : Fragment() {
    private var listener: HelpFragment.OnFragmentInteractionListener? = null

    companion object {
        //        fun newInstance() = HelpFragment()
        var myView: View? = null
    }

    private lateinit var viewModel: HelpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (myView == null) {
            Log.d(LogUtil.FEEDBACK_FRAGMENT, ": view reinflated")
            myView = inflater.inflate(R.layout.help_fragment, container, false)
        } else {
            Log.d(LogUtil.FEEDBACK_FRAGMENT, ": view restored")
        }
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HelpViewModel::class.java)

        val activity = requireActivity() as MainActivity
        activity.setSupportActionBar(toolbar_help)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_help, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onButtonPressed(Event.BackPressed)
                return true
            }
            R.id.view_in_gplay -> {
                val googlePlay =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_link)))
                startActivity(googlePlay)
                return true
            }
            R.id.version_info -> {
                Toast.makeText(
                    requireContext(),
                    "Birthdays by @vadiole, version ${BuildConfig.VERSION_NAME}\nFor more information,\nsubscribe t.me/birthdays_app",
                    Toast.LENGTH_LONG
                ).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    private fun onButtonPressed(event: Event) {
        listener?.onFragmentInteraction(event)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(event: Event)
    }
}
