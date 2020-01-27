package vadiole.birthdays.ui

import android.animation.AnimatorInflater
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_birthday.*
import kotlinx.android.synthetic.main.fragment_birthday_content.*
import org.threeten.bp.LocalDate
import vadiole.birthdays.MyViewModel
import vadiole.birthdays.R
import vadiole.birthdays.models.Birthday
import vadiole.birthdays.utils.Event
import vadiole.birthdays.utils.FragmentDestination


class BirthdayFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    var birthday = Birthday(" ", LocalDate.MIN)

    private val birthdayViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
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
            }, resources.getInteger(R.integer.nav_anim_duration).toLong())
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

        fab_edit.elevation = 0f
        fab_edit.translationZ = 0f
        fab_edit.stateListAnimator = null
        fab_edit.animate()
            .translationZ(
                resources.getDimension(R.dimen.fab_normal_elevation)
            )
            .setInterpolator(object : LinearOutSlowInInterpolator() {})
            .setStartDelay((resources.getInteger(R.integer.nav_anim_duration) * 0.75).toLong())
            .setDuration(resources.getInteger(R.integer.button_fade_in_anim_duration).toLong())
            .withLayer()
            .withEndAction {
                if (fab_edit != null) {
                    fab_edit.stateListAnimator = AnimatorInflater.loadStateListAnimator(
                        requireContext(),
                        R.animator.extended_fab_state_list_animator
                    )
                }
            }
            .start()
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
        birthdayViewModel.currentFragment = FragmentDestination.BirthdayFragment
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
        super.onAttach(context)
    }

    override fun onDetach() {
        birthdayViewModel.currentFragment = null
        listener = null
        super.onDetach()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(event: Event)
    }
}
