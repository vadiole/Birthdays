package vadiole.birthdays.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_birthday_content.*
import kotlinx.android.synthetic.main.fragment_birthday.*
import org.threeten.bp.LocalDate
import vadiole.birthdays.models.Birthday
import vadiole.birthdays.MyViewModel
import vadiole.birthdays.utils.Event
import vadiole.birthdays.R


class BirthdayFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    var birthday = Birthday(" ", LocalDate.MIN)

    private val birthdayViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_birthday, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fun initViews(b: Birthday) {
            name_birthday.text = b.name
            date_birthday.text = b.birthdayDate.toString()
        }

        birthdayViewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            birthday = it
            initViews(it)
        })

        // fix android sdk bug
        // https://github.com/ncapdevi/FragNav/issues/103

        if (!birthdayViewModel.transactionLocked) {
            birthdayViewModel.transactionLocked = true
            enableDisableView(view, false)

            val handler = Handler()

            handler.postDelayed({
                birthdayViewModel.transactionLocked = false
                enableDisableView(view, true)
            }, resources.getInteger(R.integer.nav_anim_time).toLong())
        }

        delete_btn.setOnClickListener {
            if (!Birthday.isNull(birthday)) {
                birthdayViewModel.delete(birthday)
                onButtonPressed(Event.DelPressed)
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        back_btn.setOnClickListener {
            onButtonPressed(Event.BackPressed)
        }
    }

    private fun enableDisableView(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (idx in 0 until view.childCount) {
                enableDisableView(view.getChildAt(idx), enabled)
            }
        }
    }

    private fun onButtonPressed(event: Event) {
        listener?.onFragmentInteraction(event)
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

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(event: Event)
    }
}
