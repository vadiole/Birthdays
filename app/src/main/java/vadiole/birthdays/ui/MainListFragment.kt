package vadiole.birthdays.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_main_list.*
import kotlinx.android.synthetic.main.fragment_main_list_content.*
import org.threeten.bp.LocalDate
import vadiole.birthdays.App
import vadiole.birthdays.MyViewModel
import vadiole.birthdays.R
import vadiole.birthdays.adapters.BirthdayViewHolder
import vadiole.birthdays.adapters.MainAdapter
import vadiole.birthdays.models.Birthday
import vadiole.birthdays.models.MainListDataItem
import vadiole.birthdays.utils.Event
import vadiole.birthdays.utils.FragmentDestination
import vadiole.birthdays.utils.LogUtil
import vadiole.birthdays.utils.MyDateValidator
import java.util.*
import kotlin.random.Random


class MainListFragment : Fragment(),
    BirthdayViewHolder.OnItemClickListener {
    private var listener: OnFragmentInteractionListener? = null

    private val birthdayViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
    }

    private val random = Random(System.currentTimeMillis())
    private var myView: View? = null
    private var isFullyCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        retainInstance = true    //comment because i save view in var
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (myView == null) {
            Log.d(LogUtil.MAIN_FRAGMENT, ": view reinflated")
            myView = inflater.inflate(R.layout.fragment_main_list, container, false)
            isFullyCreated = false
        } else {
            Log.d(LogUtil.MAIN_FRAGMENT, ": view restored")
        }
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isFullyCreated) {
            isFullyCreated = true
            fun initViews() {
                divider = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.divider
                )!!
                dividerItemDecoration =
                    DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
                dividerItemDecoration.setDrawable(divider)


                recycler_view.addItemDecoration(dividerItemDecoration)
                recycler_view.layoutManager = LinearLayoutManager(activity)
                recycler_view.adapter = adapter
                recycler_view.setItemViewCacheSize(20)


                initBirthdayBottomSheet()
                initMenuBottomSheet()
                initThemeDialog()
                initSnackBar(requireView())
            }

            val activity = (requireActivity() as AppCompatActivity)
            activity.setSupportActionBar(bottom_appbar)

            adapter = MainAdapter(this)


            initViews()

            setFabListeners()
            setHeaderClickListener()
            setRefreshListener()

        }
        myView = requireView()

        birthdayViewModel.allBirthdaysAndHeaders.observe(
            viewLifecycleOwner,
            Observer { birthdays ->
                birthdays?.let {
                    fillData(it)
                }
            })

        birthdayViewModel.deletedItem.observe(
            viewLifecycleOwner,
            Observer {
                if (!Birthday.isNull(it)) {
                    snackbar.show()
                }
            })
    }

    private fun fillData(newList: List<MainListDataItem>) {
        adapter.itemList = newList

        if (newList.isEmpty())
            empty_list_placeholder.visibility = View.VISIBLE
        else
            empty_list_placeholder.visibility = View.INVISIBLE
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "item $position clicked", Toast.LENGTH_SHORT).show()
        val item = adapter.itemList[position]

        if (item is MainListDataItem.BirthdayItem) {
            birthdayViewModel.setSelectedItem(item.birthday)
            listener?.onFragmentInteraction(event = Event.OpenBirthday)
        } else {
            Log.e(LogUtil.OPEN_BIRTHDAY, ": item isn't BirthdayItem")
        }
    }

    //listeners
    private fun setFabListeners() {
        //short click
        fab_new.setOnClickListener {
            if (!birthdayBottomSheetDialog.isShowing) {
                birthdayBottomSheetDialog.show()
            }
        }

        //long click
        fab_new.setOnLongClickListener {
            for (i in 1..12) {
                birthdayViewModel.insert(
                    Birthday(
                        (UUID.randomUUID().toString()).filter { c -> c != '-' }.take(
                            random.nextInt(
                                6,
                                12
                            )
                        ) + "  " + (UUID.randomUUID().toString()).filter { c -> c != '-' }.take(
                            random.nextInt(
                                8,
                                16
                            )
                        ),
                        LocalDate.of(
                            random.nextInt(1960, 2018),
                            random.nextInt(1, 13),
                            random.nextInt(1, 29)
                        )
                    )
                )
            }
            true
        }
    }

    private fun setHeaderClickListener() {
        header_main.setOnClickListener {
            recycler_view.smoothScrollToPosition(0)
        }
    }

    private fun setRefreshListener() {
        swipe_container.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                swipe_container?.isRefreshing = false
            }, 1000)
        }
    }

    //new birthday bottom sheet
    private fun initBirthdayBottomSheet() {
        birthdayBottomSheetDialog = BottomSheetDialog(requireContext())
        birthdayBottomSheetDialog.setContentView(R.layout.bottom_sheet_new_birthday)

        var selectedDate: LocalDate = LocalDate.MIN
        val editText =
            birthdayBottomSheetDialog.findViewById<TextInputEditText>(R.id.input_name)
        val btnSelectDate =
            birthdayBottomSheetDialog.findViewById<MaterialButton>(R.id.select_date_btn)
        val btnSave =
            birthdayBottomSheetDialog.findViewById<MaterialButton>(R.id.save_new_birthday_btn)

        btnSelectDate?.setOnClickListener {
            parentFragmentManager.let { it1 ->
                if (!(picker.isResumed)) {
                    picker.show(it1, picker.toString())
                }
            }
        }

        btnSave?.setOnClickListener {
            val name: String = editText?.text.toString().trim()

            if (checkIsDataValid(name, selectedDate)) {
                val birthday = Birthday(
                    name,
                    selectedDate,
                    Birthday.getYearsOld(selectedDate)
                )
                birthdayViewModel.insert(birthday)

                birthdayBottomSheetDialog.dismiss()
            }
        }

        picker.addOnPositiveButtonClickListener { selected ->
            selectedDate = LocalDate.ofEpochDay(selected / (1000 * 60 * 60 * 24))
            btnSelectDate?.text = selectedDate.toString()
        }

        birthdayBottomSheetDialog.setOnShowListener {
            setAppbarVisible(false)
        }

        birthdayBottomSheetDialog.setOnDismissListener {
            if (picker.isVisible) {
                picker.dismiss()
            }
            setAppbarVisible(true)
            selectedDate = LocalDate.MIN
            btnSelectDate?.setText(R.string.select_date_of_birthday)
            editText?.setText("")
        }
    }

    private fun checkIsDataValid(name: String, date: LocalDate): Boolean {
        return date != LocalDate.MIN && name.isNotBlank()
    }

    private fun setAppbarVisible(visible: Boolean) {
        if (visible) {
            bottom_appbar.visibility = View.VISIBLE
            fab_new.show()
        } else {
            bottom_appbar.visibility = View.INVISIBLE
            fab_new.hide()
        }
    }

    //right menu bottom sheet
    private fun initMenuBottomSheet() {
        menuBottomSheetDialog = BottomSheetDialog(requireContext())
        menuBottomSheetDialog.setContentView(R.layout.bottom_sheet_right_menu)

        menuBottomSheetDialog.findViewById<LinearLayout>(R.id.menu_theme)
            ?.setOnClickListener {
                menuBottomSheetDialog.dismiss()
                showChooseThemeDialog()
            }

        menuBottomSheetDialog.findViewById<LinearLayout>(R.id.menu_advanced_settings)
            ?.setOnLongClickListener {
                menuBottomSheetDialog.dismiss()
                birthdayViewModel.deleteAll()
                Toast.makeText(requireContext(), "Clear all", Toast.LENGTH_SHORT).show()
                true
            }
    }

    private fun showChooseThemeDialog() {
        if (!themeDialog.isShowing) {

            themeDialog.show()

            when (App.currentNightMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> themeDialog.findViewById<RadioButton>(
                    R.id.radio_btn_light
                )
                    ?.isChecked = true
                AppCompatDelegate.MODE_NIGHT_YES -> themeDialog.findViewById<RadioButton>(
                    R.id.radio_btn_dark
                )
                    ?.isChecked = true
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> themeDialog.findViewById<RadioButton>(
                    R.id.radio_btn_follow_system
                )
                    ?.isChecked = true
            }

            themeDialog.findViewById<FrameLayout>(R.id.item_follow_system)?.visibility =
                (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) View.GONE else View.VISIBLE)

            themeDialog.findViewById<FrameLayout>(R.id.item_light)
                ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
            themeDialog.findViewById<FrameLayout>(R.id.item_dark)
                ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
            themeDialog.findViewById<FrameLayout>(R.id.item_follow_system)
                ?.setOnClickListener { applyNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
        }
    }

    private fun applyNightMode(mode: Int) {
        themeDialog.dismiss()

        BirthdayFragment.myView = null
        EditBirthdayFragment.myView = null

        App.currentNightMode = mode
        App.saveNightMode(mode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    //theme dialog
    private fun initThemeDialog() {
        themeDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.alert_dialog_theme)
            .setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    //snackbar
    private fun initSnackBar(view: View) {
        snackbar = Snackbar.make(view, "Item was deleted", TIME_SNACKBAR_LENGTH)
            .setAnchorView(fab_new)
            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
            .setAction("Undo") {
                try {
                    birthdayViewModel.undoDelete()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                }
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    Log.d(LogUtil.UNDO_SNACKBAR, ": snackbar was dismissed by SWIPE")
                    super.onDismissed(transientBottomBar, event)

                    if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_ACTION || event == DISMISS_EVENT_TIMEOUT) {
                        birthdayViewModel.setRecentlyDeletedItem(Birthday.getNull())
                    }
                }
            })
    }

    //option menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                if (!menuBottomSheetDialog.isShowing) {
                    menuBottomSheetDialog.show()

                    when (App.currentNightMode) {
                        AppCompatDelegate.MODE_NIGHT_NO -> menuBottomSheetDialog.findViewById<TextView>(
                            R.id.selected_theme
                        )
                            ?.text = getString(R.string.light)
                        AppCompatDelegate.MODE_NIGHT_YES -> menuBottomSheetDialog.findViewById<TextView>(
                            R.id.selected_theme
                        )
                            ?.text = getString(R.string.dark)
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> menuBottomSheetDialog.findViewById<TextView>(
                            R.id.selected_theme
                        )
                            ?.text = getString(R.string.follow_system)
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    //lifecycle
    override fun onAttach(context: Context) {
        birthdayViewModel.currentFragment = FragmentDestination.MainListFragment
        if (context is OnFragmentInteractionListener) listener = context
        else throw RuntimeException("$context must implement OnFragmentInteractionListener")

        super.onAttach(context)
    }

    override fun onDetach() {
        birthdayViewModel.currentFragment = null
        listener = null
        super.onDetach()
    }

    override fun onResume() {
        adapter.notifyDataSetChanged()
        fab_new.visibility = View.VISIBLE
        super.onResume()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(event: Event)
    }

//    companion object {
//        @JvmStatic
//        fun newInstance() =
//            MainListFragment().apply {}
//
//
//    }

    private val TIME_SNACKBAR_LENGTH = 5000

    //views
    private lateinit var adapter: MainAdapter
    private lateinit var snackbar: Snackbar
    private lateinit var themeDialog: AlertDialog
    private lateinit var birthdayBottomSheetDialog: BottomSheetDialog
    private lateinit var menuBottomSheetDialog: BottomSheetDialog

    private lateinit var divider: Drawable
    private lateinit var dividerItemDecoration: DividerItemDecoration

    //data picker
    private val constraintsBuilder by lazy {
        CalendarConstraints.Builder()
            .setEnd(maxData)
            .setValidator(MyDateValidator())
    }
    private val builder by lazy {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.selectDataOfBirthday)
            .setTheme(R.style.AppTheme_MaterialDataPicker)
            .setCalendarConstraints(constraintsBuilder.build())

    }
    private val picker by lazy {
        builder.build()
    }
    private val maxData by lazy {
        LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)
            .toEpochDay() * (1000 * 60 * 60 * 24)
    }


}
