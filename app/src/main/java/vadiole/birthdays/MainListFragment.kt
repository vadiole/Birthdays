package vadiole.birthdays

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main_list.*
import android.view.MenuInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.content_main.*


class MainListFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private val birthdayViewModel by lazy {
        ViewModelProviders.of(this).get(BirthdayViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = (requireActivity() as AppCompatActivity)

        activity.setSupportActionBar(bottom_appbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Simple snackbar", Snackbar.LENGTH_LONG)
                .setAnchorView(fab)
                .setAction("Action") {
                    //    pickData()
                }
                .show()
        }

        val adapter = ListAdapter()
        main_birthday_list.layoutManager = LinearLayoutManager(activity)
        main_birthday_list.adapter = adapter

        birthdayViewModel.birthdayList.observe(activity, Observer {
            it.let {
                adapter.refreshBirthdays(it)

            }
        })

    }

    private fun pickData() {
        val builder: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.selectDataOfBirthday)
        val picker: MaterialDatePicker<Long> = builder.build()
        fragmentManager?.let { picker.show(it, picker.toString()) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainListFragment().apply {

            }
    }
}
