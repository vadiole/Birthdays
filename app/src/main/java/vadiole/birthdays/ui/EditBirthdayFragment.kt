package vadiole.birthdays.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.bottom_sheet_new_birthday.*
import kotlinx.android.synthetic.main.fragment_edit_birthday.*
import kotlinx.android.synthetic.main.fragment_edit_birthday_content.*
import org.threeten.bp.LocalDate
import vadiole.birthdays.MyViewModel
import vadiole.birthdays.R
import vadiole.birthdays.models.Birthday
import vadiole.birthdays.utils.Event
import vadiole.birthdays.utils.LogUtil
import java.lang.Exception


class EditBirthdayFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    private val birthdayViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
    }

    var birthday: Birthday? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (myView == null) {
            Log.d(LogUtil.EDIT_BDAY_FRAGMENT, ": view reinflated")
            myView = inflater.inflate(R.layout.fragment_edit_birthday, container, false)
        } else {
            Log.d(LogUtil.EDIT_BDAY_FRAGMENT, ": view restored")
        }
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fun initViews() {


            if (!Birthday.isNull(birthday)) {
                edit_name.setText(birthday?.name)
                edit_birthday_date.setText(birthday?.birthdayDate.toString())
                edit_phone.setText(birthday?.phoneNumber)
                edit_telegram.setText(birthday?.telegramLink)
                edit_instagram.setText(birthday?.instagramLink)
                edit_email.setText(birthday?.email)
                edit_notes.setText(birthday?.notes)
                edit_gifts.setText(birthday?.gifts)
            } else {
                onButtonPressed(Event.BackPressed)
            }
        }

        birthday = birthdayViewModel.selectedItem.value


        initViews()
        setListeners()
    }

    private fun setListeners() {
        save_edited_birthday_btn.setOnClickListener {
            if (!Birthday.isNull(birthday)) {


                saveBirthday()

            } else onButtonPressed(Event.BackPressed)
        }

        cancel_btn.setOnClickListener {
            onButtonPressed(Event.BackPressed)
        }
    }

    private fun saveBirthday() {
        birthday?.name =
            if (edit_name.text.toString().isBlank()) birthday!!.name else edit_name.text.toString().trim()

        try {
            birthday?.birthdayDate = LocalDate.parse(edit_birthday_date.text.toString())
        } catch (e: Exception) {
            Log.e(LogUtil.DATE_PARSE, ": wrong date format")
            Toast.makeText(requireContext(), "Wrong date format", Toast.LENGTH_SHORT).show()
        }

        birthday?.phoneNumber =
            edit_phone.text.toString().trim().filterNot { c -> c == ' ' }
        birthday?.telegramLink =
            edit_telegram.text.toString().trim().filterNot { c -> c == ' ' }
                .substringAfterLast('@')
                .substringAfterLast('/')
        birthday?.instagramLink =
            edit_instagram.text.toString().trim().filterNot { c -> c == ' ' }
                .substringAfterLast("com/")
        birthday?.email = edit_email.text.toString().trim().filterNot { c -> c == ' ' }
        birthday?.notes = edit_notes.text.toString().trim()
        birthday?.gifts = edit_gifts.text.toString().trim()
        birthdayViewModel.setSelectedItem(birthday!!)
        birthdayViewModel.update(birthday!!)
        onButtonPressed(Event.BackPressed)
    }


    private fun onButtonPressed(event: Event) {
        listener?.onFragmentInteraction(event)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(event: Event)
    }

    companion object {
//        @JvmStatic
//        fun newInstance() =
//            EditBirthdayFragment().apply {}

        var myView: View? = null

    }
}
