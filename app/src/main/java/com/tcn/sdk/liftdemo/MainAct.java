package com.tcn.sdk.liftdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tcn.liftboard.control.PayMethod;
import com.tcn.liftboard.control.TcnConstantLift;
import com.tcn.liftboard.control.TcnVendEventID;
import com.tcn.liftboard.control.TcnVendEventResultID;
import com.tcn.liftboard.control.TcnVendIF;
import com.tcn.liftboard.control.VendEventInfo;

public class MainAct extends TcnMainActivity {

	private static final String TAG = "MainAct";
	private static final int CMD_SET_LIFT_RESTORE_FACTORY  = 50;
	private static final int CMD_SET_LIFT_ID    = 51;
	private static final int CMD_SET_LIFT_LIGHT_OUT_STEPS    = 52;
	private static final int CMD_SET_PARAMETERS    = 53;

	private static final int CMD_SET_SLOTNO_SPRING     = 55;
	private static final int CMD_SET_SLOTNO_BELTS     = 56;
	private static final int CMD_SET_SLOTNO_ALL_SPRING     = 57;
	private static final int CMD_SET_SLOTNO_ALL_BELT     = 58;
	private static final int CMD_SET_SLOTNO_SINGLE     = 59;
	private static final int CMD_SET_SLOTNO_DOUBLE     = 60;
	private static final int CMD_SET_SLOTNO_ALL_SINGLE     = 61;
	private static final int CMD_SET_TEST_MODE    = 62;
	private static final int CMD_TEMP_CONTROL_SELECT     = 63;
	private static final int CMD_SET_GLASS_HEAT_OPEN     = 64;
	private static final int CMD_SET_GLASS_HEAT_CLOSE     = 65;

	private int singleitem=0;
	private Titlebar m_Titlebar = null;
	private Button main_serport = null;
	private ButtonEditSelectD menu_lift_clear_drive_fault = null;
	private ButtonEditSelectD menu_lift_query_drive_fault = null;
	private ButtonEditSelectD menu_lift_ship_slot = null;
	private ButtonEditSelectD menu_lift_ship_slot_test = null;
	private ButtonEditSelectD menu_lift_reqselect = null;

	private ButtonEditSelectD menu_lift_ship_check = null;
	private ButtonEditSelectD menu_lift_check_light = null;
	private ButtonEditSelectD menu_lift_back_home = null;
	private ButtonEditSelectD menu_lift_up = null;
	private ButtonEditSelectD menu_lift_cargo_door_open = null;
	private ButtonEditSelectD menu_lift_cargo_door_close = null;
	private ButtonEditSelectD menu_lift_clapboard_open = null;
	private ButtonEditSelectD menu_lift_clapboard_close = null;
	private ButtonEditSelectD menu_lift_temper_control = null;
	private ButtonEditSelectD menu_spr_set_heat_cool = null;
	private ButtonEditSelectD menu_lift_light_steps = null;
	private ButtonEditSelectD menu_lift_query_param = null;
	private ButtonEditSelectD menu_lift_set_param_select = null;

	private ButtonEditSelectD menu_lift_query_slot = null;
	private ButtonEditSelectD menu_lift_set_slot_spring = null;
	private ButtonEditSelectD menu_lift_set_slot_belts = null;
	private ButtonEditSelectD menu_lift_set_slot_spring_all = null;
	private ButtonEditSelectD menu_lift_set_slot_belts_all = null;
	private ButtonEditSelectD menu_lift_set_single_slot = null;
	private ButtonEditSelectD menu_lift_set_double_slot = null;
	private ButtonEditSelectD menu_lift_set_single_slot_all = null;
	private ButtonEditSelectD menu_lift_set_glass_heat_open = null;
	private ButtonEditSelectD menu_lift_set_glass_heat_close = null;

	private LinearLayout menu_lift_set_heat_cool_layout = null;
	private EditText menu_lift_set_heat_cool_temp = null;

	private LoadingDialog m_LoadingDialog = null;
	private OutDialog m_OutDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.background_menu_settings_layout_lift);
		TcnVendIF.getInstance().LoggerDebug(TAG, "MainAct onCreate()");
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		TcnVendIF.getInstance().registerListener(m_vendListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		TcnVendIF.getInstance().unregisterListener(m_vendListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (m_Titlebar != null) {
			m_Titlebar.removeButtonListener();
			m_Titlebar = null;
		}

		if (menu_lift_clear_drive_fault != null) {
			menu_lift_clear_drive_fault.removeButtonListener();
			menu_lift_clear_drive_fault.setOnClickListener(null);
			menu_lift_clear_drive_fault = null;
		}

		if (menu_lift_ship_slot != null) {
			menu_lift_ship_slot.removeButtonListener();
			menu_lift_ship_slot = null;
		}

		if (menu_lift_ship_slot_test != null) {
			menu_lift_ship_slot_test.removeButtonListener();
			menu_lift_ship_slot_test = null;
		}

		if (menu_lift_reqselect != null) {
			menu_lift_reqselect.removeButtonListener();
			menu_lift_reqselect = null;
		}

		if (m_OutDialog != null) {
			m_OutDialog.deInit();
			m_OutDialog = null;
		}

		if (main_serport != null) {
			main_serport.setOnClickListener(null);
			main_serport = null;
		}

		if (menu_lift_query_drive_fault != null) {
			menu_lift_query_drive_fault.removeButtonListener();
			menu_lift_query_drive_fault = null;
		}

		if (menu_lift_ship_check != null) {
			menu_lift_ship_check.removeButtonListener();
			menu_lift_ship_check = null;
		}

		if (menu_lift_check_light != null) {
			menu_lift_check_light.removeButtonListener();
			menu_lift_check_light = null;
		}

		if (menu_lift_back_home != null) {
			menu_lift_back_home.removeButtonListener();
			menu_lift_back_home = null;
		}

		if (menu_lift_up != null) {
			menu_lift_up.removeButtonListener();
			menu_lift_up = null;
		}

		if (menu_lift_cargo_door_open != null) {
			menu_lift_cargo_door_open.removeButtonListener();
			menu_lift_cargo_door_open = null;
		}

		if (menu_lift_cargo_door_close != null) {
			menu_lift_cargo_door_close.removeButtonListener();
			menu_lift_cargo_door_close = null;
		}

		if (menu_lift_clapboard_open != null) {
			menu_lift_clapboard_open.removeButtonListener();
			menu_lift_clapboard_open = null;
		}

		if (menu_lift_clapboard_close != null) {
			menu_lift_clapboard_close.removeButtonListener();
			menu_lift_clapboard_close = null;
		}

		if (menu_spr_set_heat_cool != null) {
			menu_spr_set_heat_cool.removeButtonListener();
			menu_spr_set_heat_cool = null;
		}

		if (menu_lift_temper_control != null) {
			menu_lift_temper_control.removeButtonListener();
			menu_lift_temper_control = null;
		}

		if (menu_lift_light_steps != null) {
			menu_lift_light_steps.removeButtonListener();
			menu_lift_light_steps = null;
		}
		if (menu_lift_query_param != null) {
			menu_lift_query_param.removeButtonListener();
			menu_lift_query_param = null;
		}

		if (menu_lift_set_param_select != null) {
			menu_lift_set_param_select.removeButtonListener();
			menu_lift_set_param_select = null;
		}
		if (menu_lift_query_slot != null) {
			menu_lift_query_slot.removeButtonListener();
			menu_lift_query_slot = null;
		}

		if (menu_lift_set_slot_spring != null) {
			menu_lift_set_slot_spring.removeButtonListener();
			menu_lift_set_slot_spring = null;
		}
		if (menu_lift_set_slot_belts != null) {
			menu_lift_set_slot_belts.removeButtonListener();
			menu_lift_set_slot_belts = null;
		}

		if (menu_lift_set_slot_spring_all != null) {
			menu_lift_set_slot_spring_all.removeButtonListener();
			menu_lift_set_slot_spring_all = null;
		}

		if (menu_lift_set_slot_belts_all != null) {
			menu_lift_set_slot_belts_all.removeButtonListener();
			menu_lift_set_slot_belts_all = null;
		}
		if (menu_lift_set_single_slot != null) {
			menu_lift_set_single_slot.removeButtonListener();
			menu_lift_set_single_slot = null;
		}

		if (menu_lift_set_double_slot != null) {
			menu_lift_set_double_slot.removeButtonListener();
			menu_lift_set_double_slot = null;
		}

		if (menu_lift_set_single_slot_all != null) {
			menu_lift_set_single_slot_all.removeButtonListener();
			menu_lift_set_single_slot_all = null;
		}

		if (menu_lift_set_glass_heat_open != null) {
			menu_lift_set_glass_heat_open.removeButtonListener();
			menu_lift_set_glass_heat_open = null;
		}

		if (menu_lift_set_glass_heat_close != null) {
			menu_lift_set_glass_heat_close.removeButtonListener();
			menu_lift_set_glass_heat_close = null;
		}
		m_OutDialog = null;
		m_TitleBarListener = null;
		m_ButtonEditClickListener = null;
		m_vendListener = null;
	}

	private void initView() {
		if (m_OutDialog == null) {
			m_OutDialog = new OutDialog(MainAct.this, "", getString(R.string.background_drive_setting));
			m_OutDialog.setShowTime(10000);
		}

		m_Titlebar = findViewById(R.id.menu_setttings_titlebar);
		if (m_Titlebar != null) {
			m_Titlebar.setButtonType(Titlebar.BUTTON_TYPE_BACK);
			m_Titlebar.setButtonName(R.string.background_menu_settings);
			m_Titlebar.setTitleBarListener(m_TitleBarListener);
		}

		main_serport = findViewById(R.id.main_serport);
		main_serport.setOnClickListener(m_ButtonClickListener);

		menu_lift_query_drive_fault = findViewById(R.id.menu_lift_query_drive_fault);
		if (menu_lift_query_drive_fault != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_query_drive_fault.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_query_drive_fault.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_query_drive_fault.setButtonName(getString(R.string.background_drive_query_fault));
			menu_lift_query_drive_fault.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_query_drive_fault.setButtonQueryText(getString(R.string.background_drive_query));
			menu_lift_query_drive_fault.setButtonQueryTextColor("#ffffff");
			menu_lift_query_drive_fault.setButtonDisplayTextColor("#4e5d72");
			menu_lift_query_drive_fault.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_clear_drive_fault = findViewById(R.id.menu_lift_clear_drive_fault);
		if (menu_lift_clear_drive_fault != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_clear_drive_fault.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_clear_drive_fault.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_clear_drive_fault.setButtonName(getString(R.string.background_drive_clean_fault));
			menu_lift_clear_drive_fault.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_clear_drive_fault.setButtonQueryText(R.string.background_drive_clean);
			menu_lift_clear_drive_fault.setButtonQueryTextColor("#ffffff");
			menu_lift_clear_drive_fault.setButtonDisplayTextColor("#4e5d72");
			menu_lift_clear_drive_fault.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_ship_slot = findViewById(R.id.menu_lift_ship_slot);
		if (menu_lift_ship_slot != null) {
			menu_lift_ship_slot.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_ship_slot.setButtonName("出货例子");
			menu_lift_ship_slot.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_ship_slot.setButtonQueryText("出货");
			menu_lift_ship_slot.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_ship_slot.setButtonQueryTextColor("#ffffff");
			menu_lift_ship_slot.setButtonDisplayTextColor("#4e5d72");
			menu_lift_ship_slot.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_ship_slot.setButtonListener(m_ButtonEditClickListener);

		}

		menu_lift_ship_slot_test = findViewById(R.id.menu_lift_ship_slot_test);
		if (menu_lift_ship_slot_test != null) {
			menu_lift_ship_slot_test.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_ship_slot_test.setButtonName("测试货道");
			menu_lift_ship_slot_test.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_ship_slot_test.setButtonQueryText("测试");
			menu_lift_ship_slot_test.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_ship_slot_test.setButtonQueryTextColor("#ffffff");
			menu_lift_ship_slot_test.setButtonDisplayTextColor("#4e5d72");
			menu_lift_ship_slot_test.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_ship_slot_test.setButtonListener(m_ButtonEditClickListener);

		}

		menu_lift_reqselect = findViewById(R.id.menu_lift_reqselect);
		if (menu_lift_reqselect != null) {
			menu_lift_reqselect.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_reqselect.setButtonName("选择货道");
			menu_lift_reqselect.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_reqselect.setButtonQueryText("选择");
			menu_lift_reqselect.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_reqselect.setButtonQueryTextColor("#ffffff");
			menu_lift_reqselect.setButtonDisplayTextColor("#4e5d72");
			menu_lift_reqselect.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_reqselect.setButtonListener(m_ButtonEditClickListener);

		}

		menu_lift_ship_check = findViewById(R.id.menu_lift_ship_check);
		if (menu_lift_ship_check != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_ship_check.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_ship_check.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_ship_check.setButtonQueryText(getString(R.string.background_lift_ship_check));
			menu_lift_ship_check.setButtonQueryTextColor("#ffffff");
			menu_lift_ship_check.setButtonDisplayTextColor("#4e5d72");
			menu_lift_ship_check.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_check_light = findViewById(R.id.menu_lift_check_light);
		if (menu_lift_check_light != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_check_light.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECTTWO_QUERY);
			} else {
				menu_lift_check_light.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_QUERY);
			}
			menu_lift_check_light.setButtonQueryText(getString(R.string.background_check_light));
			menu_lift_check_light.setButtonQueryTextColor("#ffffff");
			menu_lift_check_light.setButtonDisplayTextColor("#4e5d72");
			menu_lift_check_light.setLayouRatio(4,1);
			menu_lift_check_light.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_check_light.setButtonEditTextSizeSecond(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_check_light.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_back_home = findViewById(R.id.menu_lift_back_home);
		if (menu_lift_back_home != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_back_home.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_back_home.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}

			menu_lift_back_home.setButtonQueryText(getString(R.string.background_lift_back_home));
			menu_lift_back_home.setButtonQueryTextColor("#ffffff");
			menu_lift_back_home.setButtonDisplayTextColor("#4e5d72");
			menu_lift_back_home.setButtonListener(m_ButtonEditClickListener);

		}

		menu_lift_up = findViewById(R.id.menu_lift_up);
		if (menu_lift_up != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_up.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECTTWO_QUERY);
			} else {
				menu_lift_up.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_QUERY);
			}
			menu_lift_up.setButtonQueryText(getString(R.string.background_lift_up));
			menu_lift_up.setButtonQueryTextColor("#ffffff");
			menu_lift_up.setButtonDisplayTextColor("#4e5d72");
			menu_lift_up.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_up.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_cargo_door_open = findViewById(R.id.menu_lift_cargo_door_open);
		if (menu_lift_cargo_door_open != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_cargo_door_open.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_cargo_door_open.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_cargo_door_open.setButtonName(getString(R.string.background_lift_cargo_door_control));
			menu_lift_cargo_door_open.setButtonQueryText(getString(R.string.background_lift_open));
			menu_lift_cargo_door_open.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(16));

			menu_lift_cargo_door_open.setButtonQueryTextColor("#ffffff");
			menu_lift_cargo_door_open.setButtonDisplayTextColor("#4e5d72");
			menu_lift_cargo_door_open.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_cargo_door_close = findViewById(R.id.menu_lift_cargo_door_close);
		if (menu_lift_cargo_door_close != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_cargo_door_close.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_cargo_door_close.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_cargo_door_close.setButtonName(getString(R.string.background_lift_cargo_door_control));
			menu_lift_cargo_door_close.setButtonQueryText(getString(R.string.background_lift_close));

			menu_lift_cargo_door_close.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));

			menu_lift_cargo_door_close.setButtonQueryTextColor("#ffffff");
			menu_lift_cargo_door_close.setButtonDisplayTextColor("#4e5d72");
			menu_lift_cargo_door_close.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_clapboard_open = findViewById(R.id.menu_lift_clapboard_open);
		if (menu_lift_clapboard_open != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_clapboard_open.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_clapboard_open.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_clapboard_open.setButtonName(getString(R.string.background_lift_clapboard_control));
			menu_lift_clapboard_open.setButtonQueryText(getString(R.string.background_lift_open));

			menu_lift_clapboard_open.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));

			menu_lift_clapboard_open.setButtonQueryTextColor("#ffffff");
			menu_lift_clapboard_open.setButtonDisplayTextColor("#4e5d72");
			menu_lift_clapboard_open.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_clapboard_close = findViewById(R.id.menu_lift_clapboard_close);
		if (menu_lift_clapboard_close != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_clapboard_close.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
			} else {
				menu_lift_clapboard_close.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_clapboard_close.setButtonName(getString(R.string.background_lift_clapboard_control));
			menu_lift_clapboard_close.setButtonQueryText(getString(R.string.background_lift_close));

			menu_lift_clapboard_close.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));

			menu_lift_clapboard_close.setButtonQueryTextColor("#ffffff");
			menu_lift_clapboard_close.setButtonDisplayTextColor("#4e5d72");
			menu_lift_clapboard_close.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_temper_control = findViewById(R.id.menu_lift_temper_control);
		if (menu_lift_temper_control != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_temper_control.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECTTWO_QUERY);
			} else {
				menu_lift_temper_control.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_QUERY);
			}
			menu_lift_temper_control.setVisibility(View.GONE);

			menu_lift_temper_control.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_temper_control.setButtonName(getString(R.string.background_lift_temper_control));
			menu_lift_temper_control.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_temper_control.setButtonQueryTextColor("#ffffff");
			menu_lift_temper_control.setButtonDisplayTextColor("#4e5d72");
			menu_lift_temper_control.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_temper_control.setButtonEditTextSizeSecond(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_temper_control.setButtonListener(m_ButtonEditClickListener);
		}

		menu_spr_set_heat_cool = findViewById(R.id.menu_lift_set_heat_cool);
		if (menu_spr_set_heat_cool != null) {
			menu_spr_set_heat_cool.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_QUERY);
			menu_spr_set_heat_cool.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_spr_set_heat_cool.setButtonName(R.string.background_spring_set_heat_cool);
			menu_spr_set_heat_cool.setButtonQueryText(getString(R.string.background_drive_set));
			menu_spr_set_heat_cool.setButtonQueryTextColor("#ffffff");
			menu_spr_set_heat_cool.setButtonDisplayTextColor("#4e5d72");
			menu_spr_set_heat_cool.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_heat_cool_layout = findViewById(R.id.menu_lift_set_heat_cool_layout);
		menu_lift_set_heat_cool_layout.setVisibility(View.GONE);
		menu_lift_set_heat_cool_temp = findViewById(R.id.menu_lift_set_heat_cool_temp);
		menu_lift_set_heat_cool_temp.setTextSize(TcnVendIF.getInstance().getFitScreenSize(18));
		menu_lift_set_heat_cool_temp.setText(String.valueOf(TcnVendIF.getInstance().getTempControlTemp()));
		if (TcnVendIF.getInstance().getTempControl() == 1) {    //制冷
			menu_lift_set_heat_cool_layout.setVisibility(View.VISIBLE);
			menu_spr_set_heat_cool.getButtonEditSecond().setText(UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[0]);
		} else if (TcnVendIF.getInstance().getTempControl() == 2) { //加热
			menu_lift_set_heat_cool_layout.setVisibility(View.VISIBLE);
			menu_spr_set_heat_cool.getButtonEditSecond().setText(UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[1]);
		} else {
			menu_lift_set_heat_cool_layout.setVisibility(View.GONE);
			menu_spr_set_heat_cool.getButtonEditSecond().setText(UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[2]);
		}

		menu_lift_light_steps = findViewById(R.id.menu_lift_light_steps);
		if (menu_lift_light_steps != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_light_steps.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_INPUT_QUERY);
			} else {
				menu_lift_light_steps.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			}
			menu_lift_light_steps.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_light_steps.setButtonName(getString(R.string.background_lift_set_light_steps));
			menu_lift_light_steps.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_light_steps.setButtonQueryTextColor("#ffffff");
			menu_lift_light_steps.setButtonDisplayTextColor("#4e5d72");
			menu_lift_light_steps.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_light_steps.setButtonInputTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_light_steps.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_light_steps.setButtonListener(m_ButtonEditClickListener);
		}


		menu_lift_query_param = findViewById(R.id.menu_lift_query_param);
		if (menu_lift_query_param != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_query_param.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECTTWO_QUERY);
			} else {
				menu_lift_query_param.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_QUERY);
			}
			menu_lift_query_param.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_query_param.setButtonName(getString(R.string.background_lift_query_params));
			menu_lift_query_param.setButtonQueryText(getString(R.string.background_drive_query));
			menu_lift_query_param.setButtonQueryTextColor("#ffffff");
			menu_lift_query_param.setButtonDisplayTextColor("#4e5d72");
			menu_lift_query_param.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_query_param.setButtonEditTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_query_param.setButtonEditTextSizeSecond(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_query_param.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_param_select = findViewById(R.id.menu_lift_set_param_select);
		if (menu_lift_set_param_select != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_set_param_select.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECTTWO_INPUT_QUERY);
			} else {
				menu_lift_set_param_select.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_SECOND_INPUT_QUERY);
			}
			menu_lift_set_param_select.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_param_select.setButtonName(getString(R.string.background_lift_set_params));
			menu_lift_set_param_select.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_param_select.setButtonQueryTextColor("#ffffff");
			menu_lift_set_param_select.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_param_select.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_param_select.setButtonEditTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_param_select.setButtonEditTextSizeSecond(TcnVendIF.getInstance().getFitScreenSize(16));
			// menu_lift_set_param_select.setButtonDisplayVisibility(View.GONE);
//            menu_lift_set_param_select.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_set_param_select.setButtonListener(m_ButtonEditClickListener);
		}


		menu_lift_query_slot = findViewById(R.id.menu_lift_query_slot);
		if (menu_lift_query_slot != null) {
			menu_lift_query_slot.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_query_slot.setButtonName(getString(R.string.background_drive_query_slot));
			menu_lift_query_slot.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_query_slot.setButtonQueryText(getString(R.string.background_drive_query));
			menu_lift_query_slot.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_query_slot.setButtonQueryTextColor("#ffffff");
			menu_lift_query_slot.setButtonDisplayTextColor("#4e5d72");
			menu_lift_query_slot.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_query_slot.setButtonListener(m_ButtonEditClickListener);

		}

		menu_lift_set_slot_spring = findViewById(R.id.menu_lift_set_slot_spring);
		if (menu_lift_set_slot_spring != null) {
			menu_lift_set_slot_spring.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_set_slot_spring.setButtonName(getString(R.string.background_spring_set_slot_spring));
			menu_lift_set_slot_spring.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_slot_spring.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_slot_spring.setButtonQueryTextColor("#ffffff");
			menu_lift_set_slot_spring.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_slot_spring.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_slot_spring.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_set_slot_spring.setButtonListener(m_ButtonEditClickListener);
		}


		menu_lift_set_slot_belts = findViewById(R.id.menu_lift_set_slot_belts);
		if (menu_lift_set_slot_belts != null) {
			menu_lift_set_slot_belts.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_set_slot_belts.setButtonName(getString(R.string.background_spring_set_slot_belts));
			menu_lift_set_slot_belts.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_slot_belts.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_slot_belts.setButtonQueryTextColor("#ffffff");
			menu_lift_set_slot_belts.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_slot_belts.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_slot_belts.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_set_slot_belts.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_slot_spring_all = findViewById(R.id.menu_lift_set_slot_spring_all);
		if (menu_lift_set_slot_spring_all != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_set_slot_spring_all.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
				menu_lift_set_slot_spring_all.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
				menu_lift_set_slot_spring_all.setButtonDisplayTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			} else {
				menu_lift_set_slot_spring_all.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}

			menu_lift_set_slot_spring_all.setButtonName(getString(R.string.background_spring_set_slot_spring_all));
			menu_lift_set_slot_spring_all.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_slot_spring_all.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_slot_spring_all.setButtonQueryTextColor("#ffffff");
			menu_lift_set_slot_spring_all.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_slot_spring_all.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_slot_belts_all = findViewById(R.id.menu_lift_set_slot_belts_all);
		if (menu_lift_set_slot_belts_all != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_set_slot_belts_all.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
				menu_lift_set_slot_belts_all.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
				menu_lift_set_slot_belts_all.setButtonDisplayTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			} else {
				menu_lift_set_slot_belts_all.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}

			menu_lift_set_slot_belts_all.setButtonName(getString(R.string.background_spring_set_slot_belts_all));
			menu_lift_set_slot_belts_all.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_slot_belts_all.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_slot_belts_all.setButtonQueryTextColor("#ffffff");
			menu_lift_set_slot_belts_all.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_slot_belts_all.setButtonListener(m_ButtonEditClickListener);
		}


		menu_lift_set_single_slot = findViewById(R.id.menu_lift_set_single_slot);
		if (menu_lift_set_single_slot != null) {
			menu_lift_set_single_slot.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_set_single_slot.setButtonName(getString(R.string.background_spring_set_single_slot));
			menu_lift_set_single_slot.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_single_slot.setButtonQueryText(getString(R.string.background_spring_set_single));
			menu_lift_set_single_slot.setButtonQueryTextColor("#ffffff");
			menu_lift_set_single_slot.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_single_slot.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_single_slot.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_set_single_slot.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_double_slot = findViewById(R.id.menu_lift_set_double_slot);
		if (menu_lift_set_double_slot != null) {
			menu_lift_set_double_slot.setButtonType(ButtonEditSelectD.BUTTON_TYPE_EDIT_QUERY);
			menu_lift_set_double_slot.setButtonName(getString(R.string.background_spring_set_double_slot));
			menu_lift_set_double_slot.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_double_slot.setButtonQueryText(getString(R.string.background_spring_set_double));
			menu_lift_set_double_slot.setButtonQueryTextColor("#ffffff");
			menu_lift_set_double_slot.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_double_slot.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			menu_lift_set_double_slot.setInputTypeInput(InputType.TYPE_CLASS_NUMBER);
			menu_lift_set_double_slot.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_single_slot_all = findViewById(R.id.menu_lift_set_single_slot_all);
		if (menu_lift_set_single_slot_all != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_set_single_slot_all.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
				menu_lift_set_single_slot_all.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
				menu_lift_set_single_slot_all.setButtonDisplayTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			} else {
				menu_lift_set_single_slot_all.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}

			menu_lift_set_single_slot_all.setButtonName(getString(R.string.background_spring_set_single_slot_all));
			menu_lift_set_single_slot_all.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_single_slot_all.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_single_slot_all.setButtonQueryTextColor("#ffffff");
			menu_lift_set_single_slot_all.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_single_slot_all.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_glass_heat_open = findViewById(R.id.menu_lift_set_glass_heat_open);
		if (menu_lift_set_glass_heat_open != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_set_glass_heat_open.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
				menu_lift_set_glass_heat_open.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
				menu_lift_set_glass_heat_open.setButtonDisplayTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			} else {
				menu_lift_set_glass_heat_open.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}
			menu_lift_set_glass_heat_open.setButtonName(getString(R.string.background_spring_glass_heat_open));
			menu_lift_set_glass_heat_open.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_glass_heat_open.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_glass_heat_open.setButtonQueryTextColor("#ffffff");
			menu_lift_set_glass_heat_open.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_glass_heat_open.setButtonListener(m_ButtonEditClickListener);
		}

		menu_lift_set_glass_heat_close = findViewById(R.id.menu_lift_set_glass_heat_close);
		if (menu_lift_set_glass_heat_close != null) {
			if (UIComBack.getInstance().isMutiGrpElevator()) {
				menu_lift_set_glass_heat_close.setButtonType(ButtonEditSelectD.BUTTON_TYPE_SELECT_QUERY);
				menu_lift_set_glass_heat_close.setButtonQueryTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
				menu_lift_set_glass_heat_close.setButtonDisplayTextSize(TcnVendIF.getInstance().getFitScreenSize(16));
			} else {
				menu_lift_set_glass_heat_close.setButtonType(ButtonEditSelectD.BUTTON_TYPE_QUERY);
			}

			menu_lift_set_glass_heat_close.setButtonName(getString(R.string.background_spring_glass_heat_close));
			menu_lift_set_glass_heat_close.setButtonNameTextSize(TcnVendIF.getInstance().getFitScreenSize(20));
			menu_lift_set_glass_heat_close.setButtonQueryText(getString(R.string.background_drive_set));
			menu_lift_set_glass_heat_close.setButtonQueryTextColor("#ffffff");
			menu_lift_set_glass_heat_close.setButtonDisplayTextColor("#4e5d72");
			menu_lift_set_glass_heat_close.setButtonListener(m_ButtonEditClickListener);
		}

	}

	private void selectLiftParam(int data) {
		if (menu_lift_query_param != null) {
			menu_lift_query_param.setButtonDisplayText(String.valueOf(data));
		}
	}

	private void setDialogShow() {
		if (m_OutDialog != null) {
			m_OutDialog.setShowTime(5);
			m_OutDialog.setTitle(getString(R.string.background_tip_wait_amoment));
			m_OutDialog.show();
		}
	}

	private void showSetConfirm(final int cmdType,final String grp,final String data1,final String data2) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.background_drive_modify_ask));
		builder.setPositiveButton(getString(R.string.background_backgroound_ensure), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				int showTimeOut = 5;
				if (CMD_SET_LIFT_RESTORE_FACTORY == cmdType) {
					if (TcnVendIF.getInstance().isDigital(grp)) {
						TcnVendIF.getInstance().reqFactoryReset(Integer.valueOf(grp));
					} else {
						TcnVendIF.getInstance().reqFactoryReset(-1);
					}
				} else if (CMD_SET_LIFT_ID == cmdType) {
					showTimeOut = 1;
					if (TcnVendIF.getInstance().isDigital(grp) && TcnVendIF.getInstance().isDigital(data1)) {
						TcnVendIF.getInstance().reqSetId(Integer.valueOf(grp),Integer.valueOf(data1));
					} else {
						if (TcnVendIF.getInstance().isDigital(data1)) {
							TcnVendIF.getInstance().reqSetId(-1,Integer.valueOf(data1));
						}

					}
				} else if (CMD_SET_LIFT_LIGHT_OUT_STEPS == cmdType) {
					if (TcnVendIF.getInstance().isDigital(grp) && TcnVendIF.getInstance().isDigital(data1)) {
						TcnVendIF.getInstance().reqSetLightOutSteps(Integer.valueOf(grp),Integer.valueOf(data1));
					} else {
						if (TcnVendIF.getInstance().isDigital(data1)) {
							TcnVendIF.getInstance().reqSetLightOutSteps(-1,Integer.valueOf(data1));
						}

					}
				} else if (CMD_SET_PARAMETERS == cmdType) {
					TcnVendIF.getInstance().LoggerDebug(TAG, "CMD_SET_PARAMETERS data1: "+data1+" data2: "+data2);
					if (TcnVendIF.getInstance().isDigital(grp) && TcnVendIF.getInstance().isNumeric(data2)) {
						TcnVendIF.getInstance().LoggerDebug(TAG, "CMD_SET_PARAMETERS 1 data2: "+data2);
						TcnVendIF.getInstance().reqSetParameters(Integer.valueOf(grp),UIComBack.getInstance().getQueryParameters(data1),data2);
					} else {
						if (TcnVendIF.getInstance().isNumeric(data2)) {
							TcnVendIF.getInstance().LoggerDebug(TAG, "CMD_SET_PARAMETERS 2 data2: "+data2);
							TcnVendIF.getInstance().reqSetParameters(-1,UIComBack.getInstance().getQueryParameters(data1),data2);
						}

					}
				} else if (CMD_SET_SLOTNO_SPRING == cmdType) {
					if (TcnVendIF.getInstance().isDigital(data1)) {
						TcnVendIF.getInstance().reqSetSpringSlot(Integer.valueOf(data1));
					}

				} else if (CMD_SET_SLOTNO_BELTS == cmdType) {
					if (TcnVendIF.getInstance().isDigital(data1)) {
						TcnVendIF.getInstance().reqSetBeltsSlot(Integer.valueOf(data1));
					}

				} else if (CMD_SET_SLOTNO_ALL_SPRING == cmdType) {
					if (TcnVendIF.getInstance().isDigital(grp)) {
						TcnVendIF.getInstance().reqSpringAllSlot(Integer.valueOf(grp));
					} else {
						TcnVendIF.getInstance().reqSpringAllSlot(-1);
					}

				} else if (CMD_SET_SLOTNO_ALL_BELT == cmdType) {
					if (TcnVendIF.getInstance().isDigital(grp)) {
						TcnVendIF.getInstance().reqBeltsAllSlot(Integer.valueOf(grp));
					} else {
						TcnVendIF.getInstance().reqBeltsAllSlot(-1);
					}

				} else if (CMD_SET_SLOTNO_SINGLE == cmdType) {
					if (TcnVendIF.getInstance().isDigital(data1)) {
						TcnVendIF.getInstance().reqSingleSlot(Integer.valueOf(data1));
					}
				} else if (CMD_SET_SLOTNO_DOUBLE == cmdType) {
					if (TcnVendIF.getInstance().isDigital(data1)) {
						TcnVendIF.getInstance().reqDoubleSlot(Integer.valueOf(data1));
					}
				} else if (CMD_SET_SLOTNO_ALL_SINGLE == cmdType) {
					if (TcnVendIF.getInstance().isDigital(grp)) {
						TcnVendIF.getInstance().reqSingleAllSlot(Integer.valueOf(grp));
					} else {
						TcnVendIF.getInstance().reqSingleAllSlot(-1);
					}

				} else if (CMD_SET_TEST_MODE == cmdType) {
					showTimeOut = 60*60;
					if (TcnVendIF.getInstance().isDigital(grp)) {
						TcnVendIF.getInstance().reqTestMode(Integer.valueOf(grp));
					} else {
						TcnVendIF.getInstance().reqTestMode(-1);
					}

				} else if (CMD_SET_GLASS_HEAT_OPEN == cmdType) {
					if (TcnVendIF.getInstance().isDigital(grp)) {
						TcnVendIF.getInstance().reqSetGlassHeatEnable(Integer.valueOf(grp),true);
					} else {
						TcnVendIF.getInstance().reqSetGlassHeatEnable(-1,true);
					}
				} else if (CMD_SET_GLASS_HEAT_CLOSE == cmdType) {
					if (TcnVendIF.getInstance().isDigital(grp)) {
						TcnVendIF.getInstance().reqSetGlassHeatEnable(Integer.valueOf(grp),false);
					} else {
						TcnVendIF.getInstance().reqSetGlassHeatEnable(-1,false);
					}
				}
				else {

				}


				if (m_OutDialog != null) {
					m_OutDialog.setShowTime(showTimeOut);
					m_OutDialog.show();
				}

			}
		});
		builder.setNegativeButton(getString(R.string.background_backgroound_cancel), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		builder.show();
	}

	private void showSelectDialog(final int type, String title,final EditText v, String selectData,final String[] str) {
		if (null == str) {
			return;
		}
		int checkedItem = -1;
		if ((selectData != null) && (selectData.length() > 0)) {
			for (int i = 0; i < str.length; i++) {
				if (str[i].equals(selectData)) {
					checkedItem = i;
					break;
				}
			}
		}

		singleitem=0;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setSingleChoiceItems(str, checkedItem, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				singleitem=which;
			}
		});
		builder.setPositiveButton(getString(R.string.background_backgroound_ensure), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				v.setText(str[singleitem]);
				if (CMD_TEMP_CONTROL_SELECT == type) {
					if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[0]).equals(str[singleitem])) {
						menu_lift_set_heat_cool_layout.setVisibility(View.VISIBLE);
					} else if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[1]).equals(str[singleitem])) {
						menu_lift_set_heat_cool_layout.setVisibility(View.VISIBLE);
					} else {
						menu_lift_set_heat_cool_layout.setVisibility(View.GONE);
					}
				}
			}
		});
		builder.setNegativeButton(getString(R.string.background_backgroound_cancel), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		builder.show();
	}

	private MenuSetTitleBarListener m_TitleBarListener = new MenuSetTitleBarListener();
	private class MenuSetTitleBarListener implements Titlebar.TitleBarListener {

		@Override
		public void onClick(View v, int buttonId) {
			if (Titlebar.BUTTON_ID_BACK == buttonId) {
				MainAct.this.finish();
			}
		}
	}

	private ButtonClickListener m_ButtonClickListener = new ButtonClickListener();
	private class ButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			int mId = view.getId();
			TcnVendIF.getInstance().LoggerDebug(TAG, "onClick()");
			if (R.id.main_serport == mId) {
				TcnVendIF.getInstance().LoggerDebug(TAG, "onClick() to SerialPortSetting");
				Intent in = new Intent(MainAct.this, SerialPortSetting.class);
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(in);
			}

		}
	}

	private ButtonEditClickListener m_ButtonEditClickListener= new ButtonEditClickListener();
	private class ButtonEditClickListener implements ButtonEditSelectD.ButtonListener {
		@Override
		public void onClick(View v, int buttonId) {
			if (null == v) {
				return;
			}
			if (TcnUtilityUI.isFastClick()) {
				return;
			}
			int id = v.getId();
			if (R.id.menu_lift_query_drive_fault == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_query_drive_fault.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_query_drive_fault.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqQueryStatus(UIComBack.getInstance().getGroupElevatorId(strParam));
						}
					} else {
						TcnVendIF.getInstance().reqQueryStatus(-1);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_query_drive_fault.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_clear_drive_fault == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_clear_drive_fault.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_clear_drive_fault.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqCleanDriveFaults(UIComBack.getInstance().getGroupElevatorId(strParam));
						}
					} else {
						TcnVendIF.getInstance().reqCleanDriveFaults(-1);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_clear_drive_fault.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_ship_slot == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					String strParam = menu_lift_ship_slot.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						int slotNo = Integer.valueOf(strParam);//出货的货道号
						String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。
						String amount = "0.1";    //支付的金额（元）,自己修改
						String tradeNo = "1811020095201811150126888";//支付订单号，每次出货，订单号不能一样，此处自己修改。
						TcnVendIF.getInstance().reqShip(slotNo,shipMethod,amount,tradeNo);
					}
				}
			} else if (R.id.menu_lift_ship_slot_test == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					String strParam = menu_lift_ship_slot_test.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						TcnVendIF.getInstance().reqShipTest(1);
					}
				}
			} else if (R.id.menu_lift_reqselect == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					String strParam = menu_lift_reqselect.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						TcnVendIF.getInstance().reqSelectSlotNo(Integer.valueOf(strParam));
					}
				}
			}
			else if (R.id.menu_lift_ship_check == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_ship_check.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_ship_check.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqDetectShip(UIComBack.getInstance().getGroupElevatorId(strParam));
						}
					} else {
						TcnVendIF.getInstance().reqDetectShip(-1);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_ship_check.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_check_light == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_check_light.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_check_light.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							String strParamSecond = menu_lift_check_light.getButtonEditTextSecond();
							if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
								TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_check_direction));
							} else {
								TcnVendIF.getInstance().reqDetectLight(UIComBack.getInstance().getGroupElevatorId(strParam),strParamSecond);
							}

						}
					} else {
						String strParamSecond = menu_lift_check_light.getButtonEditTextSecond();
						if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_check_direction));
						} else {
							TcnVendIF.getInstance().reqDetectLight(-1,strParamSecond);
						}
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_check_light.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
					showSelectDialog(-1,getString(R.string.background_lift_tips_select_cabinetno_floor),menu_lift_check_light.getButtonEditSecond(), "",TcnVendIF.getInstance().getCheckLightSelectData());
				}
				else {

				}
			} else if (R.id.menu_lift_back_home == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_back_home.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_back_home.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqBackHome(UIComBack.getInstance().getGroupElevatorId(strParam));
						}
					} else {
						TcnVendIF.getInstance().reqBackHome(-1);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_back_home.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_up == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_up.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_up.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							String strParamSecond = menu_lift_up.getButtonEditTextSecond();
							if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
								TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_floor_no));
							} else {
								if (strParamSecond.contains("~")) {
									int index = strParamSecond.indexOf("~");
									strParamSecond = strParamSecond.substring(0,index).trim();
								}
								TcnVendIF.getInstance().reqLifterUp(UIComBack.getInstance().getGroupElevatorId(strParam),Integer.parseInt(strParamSecond));
							}

						}
					} else {
						String strParamSecond = menu_lift_up.getButtonEditTextSecond();
						if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_floor_no));
						} else {
							if (strParamSecond.contains("~")) {
								int index = strParamSecond.indexOf("~");
								strParamSecond = strParamSecond.substring(0,index).trim();
							}
							TcnVendIF.getInstance().reqLifterUp(-1,Integer.parseInt(strParamSecond));
						}
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_up.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
					showSelectDialog(-1,getString(R.string.background_lift_tips_select_floor_no),menu_lift_up.getButtonEditSecond(), "",TcnVendIF.getInstance().getFloorAllData());
				}
				else {

				}
			} else if (R.id.menu_lift_cargo_door_open == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_cargo_door_open.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_cargo_door_open.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqTakeGoodsDoorControl(UIComBack.getInstance().getGroupElevatorId(strParam),true);
						}
					} else {
						TcnVendIF.getInstance().reqTakeGoodsDoorControl(-1,true);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_cargo_door_open.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_cargo_door_close == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_cargo_door_close.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_cargo_door_close.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqTakeGoodsDoorControl(UIComBack.getInstance().getGroupElevatorId(strParam),false);
						}
					} else {
						TcnVendIF.getInstance().reqTakeGoodsDoorControl(-1,false);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_cargo_door_close.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_clapboard_open == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_clapboard_open.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_clapboard_open.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqClapboardSwitch(UIComBack.getInstance().getGroupElevatorId(strParam),true);
						}
					} else {
						TcnVendIF.getInstance().reqClapboardSwitch(-1,true);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_clapboard_open.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_clapboard_close == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_clapboard_close.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_clapboard_close.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							TcnVendIF.getInstance().reqClapboardSwitch(UIComBack.getInstance().getGroupElevatorId(strParam),false);
						}
					} else {
						TcnVendIF.getInstance().reqClapboardSwitch(-1,false);
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_clapboard_close.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_set_heat_cool == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_spr_set_heat_cool.setButtonDisplayText("");
					String strParamSecond = menu_spr_set_heat_cool.getButtonEditTextSecond();
					if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_control_action));
					} else {
						if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[0]).equals(strParamSecond)) {
							String temp = menu_lift_set_heat_cool_temp.getText().toString();
							if (!TcnVendIF.getInstance().isNumeric(temp)) {
								TcnUtilityUI.getToast(MainAct.this, "请填入温度值");
								return;
							}

							TcnVendIF.getInstance().LoggerDebug(TAG, "setConfigRfgHeat temp: "+temp);

//                            TcnVendIF.getInstance().reqTemperControl(-1,strParamSecond,Integer.parseInt(temp),
//                                    Integer.parseInt(startTime),Integer.parseInt(endTime));
							TcnVendIF.getInstance().reqOpenCoolSpring(-1,Integer.parseInt(temp));
						} else if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[1]).equals(strParamSecond)) {
							String temp = menu_lift_set_heat_cool_temp.getText().toString();
							if (!TcnVendIF.getInstance().isNumeric(temp)) {
								TcnUtilityUI.getToast(MainAct.this, "请填入温度值");
								return;
							}
//                            TcnVendIF.getInstance().reqTemperControl(-1,strParamSecond,Integer.parseInt(temp),
//                                    Integer.parseInt(startTime),Integer.parseInt(endTime));
							TcnVendIF.getInstance().reqHeatSpring(-1,Integer.parseInt(temp));
						} else if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[2]).equals(strParamSecond)) {
							TcnVendIF.getInstance().reqCloseCoolHeat(-1);
						} else {

						}
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_spr_set_heat_cool.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
					showSelectDialog(CMD_TEMP_CONTROL_SELECT,getString(R.string.background_lift_tips_select_control_action),menu_spr_set_heat_cool.getButtonEditSecond(), "", UIComBack.HEAT_COOL_OFF_SWITCH_SELECT);
				}
				else {

				}

			}
			else if (R.id.menu_lift_temper_control == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_temper_control.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_temper_control.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							String strParamSecond = menu_lift_temper_control.getButtonEditTextSecond();
							if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
								TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_control_action));
							} else {
								if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[0]).equals(strParamSecond)) {
									String temp = menu_lift_set_heat_cool_temp.getText().toString();
									if (!TcnVendIF.getInstance().isNumeric(temp)) {
										TcnUtilityUI.getToast(MainAct.this, "请填入温度值");
										return;
									}
									TcnVendIF.getInstance().reqOpenCoolSpring(UIComBack.getInstance().getGroupElevatorId(strParam),Integer.parseInt(temp));
								} else if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[1]).equals(strParamSecond)) {
									String temp = menu_lift_set_heat_cool_temp.getText().toString();
									if (!TcnVendIF.getInstance().isNumeric(temp)) {
										TcnUtilityUI.getToast(MainAct.this, "请填入温度值");
										return;
									}
									TcnVendIF.getInstance().reqHeatSpring(UIComBack.getInstance().getGroupElevatorId(strParam),Integer.parseInt(temp));
								} else if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[2]).equals(strParamSecond)) {
									TcnVendIF.getInstance().reqCloseCoolHeat(UIComBack.getInstance().getGroupElevatorId(strParam));
								} else {

								}
							}

						}
					} else {
						String strParamSecond = menu_lift_temper_control.getButtonEditTextSecond();
						if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_control_action));
						} else {
							if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[0]).equals(strParamSecond)) {
								String temp = menu_lift_set_heat_cool_temp.getText().toString();
								if (!TcnVendIF.getInstance().isNumeric(temp)) {
									TcnUtilityUI.getToast(MainAct.this, "请填入温度值");
									return;
								}
//                                TcnVendIF.getInstance().reqTemperControl(-1,strParamSecond,Integer.parseInt(temp),
//                                        Integer.parseInt(startTime),Integer.parseInt(endTime));
								TcnVendIF.getInstance().reqOpenCoolSpring(-1,Integer.parseInt(temp));
							} else if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[1]).equals(strParamSecond)) {
								String temp = menu_lift_set_heat_cool_temp.getText().toString();
								if (!TcnVendIF.getInstance().isNumeric(temp)) {
									TcnUtilityUI.getToast(MainAct.this, "请填入温度值");
									return;
								}
//                                TcnVendIF.getInstance().reqTemperControl(-1,strParamSecond,Integer.parseInt(temp),
//                                        Integer.parseInt(startTime),Integer.parseInt(endTime));
								TcnVendIF.getInstance().reqHeatSpring(-1,Integer.parseInt(temp));
							} else if ((UIComBack.HEAT_COOL_OFF_SWITCH_SELECT[2]).equals(strParamSecond)) {
								TcnVendIF.getInstance().reqCloseCoolHeat(-1);
							} else {

							}
						}
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_temper_control.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
					showSelectDialog(CMD_TEMP_CONTROL_SELECT,getString(R.string.background_lift_tips_select_control_action),menu_lift_temper_control.getButtonEditSecond(), "",UIComBack.HEAT_COOL_OFF_SWITCH_SELECT);
				}
				else {

				}
			} else if (R.id.menu_lift_light_steps == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_light_steps.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_light_steps.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							String strParamSecond = menu_lift_light_steps.getButtonEditInputText();
							if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
								TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_set_value_canont_empty));
							} else {
								showSetConfirm(CMD_SET_LIFT_LIGHT_OUT_STEPS,String.valueOf(UIComBack.getInstance().getGroupElevatorId(strParam)),strParamSecond,"");
							}

						}
					} else {
						String strParamSecond = menu_lift_light_steps.getButtonEditInputText();
						if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_set_value_canont_empty));
						} else {
							showSetConfirm(CMD_SET_LIFT_LIGHT_OUT_STEPS,"",strParamSecond,"");
						}
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_light_steps.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_query_param == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_query_param.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_query_param.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							String strParamSecond = menu_lift_query_param.getButtonEditTextSecond();
							if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
								TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_query_parameters));
							} else {
								TcnVendIF.getInstance().reqQueryParameters(UIComBack.getInstance().getGroupElevatorId(strParam),UIComBack.getInstance().getQueryParameters(strParamSecond));
							}
						}
					} else {
						String strParamSecond = menu_lift_query_param.getButtonEditTextSecond();
						if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_query_parameters));
						} else {
							TcnVendIF.getInstance().reqQueryParameters(-1,UIComBack.getInstance().getQueryParameters(strParamSecond));
						}
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_query_param.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
					showSelectDialog(-1,getString(R.string.background_lift_tips_select_query_parameters),menu_lift_query_param.getButtonEditSecond(), "", TcnConstantLift.LIFT_QUERY_PARAM);

				}
				else {

				}
			} else if (R.id.menu_lift_set_param_select == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_param_select.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_set_param_select.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							String strParamSecond = menu_lift_set_param_select.getButtonEditTextSecond();
							if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
								TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_set_parameters));
							} else {
								String strInputValue = menu_lift_set_param_select.getButtonEditInputText();
								if ((null == strInputValue) || (strInputValue.length() < 1)) {
									TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_set_value_canont_empty));
								} else {
									showSetConfirm(CMD_SET_PARAMETERS,String.valueOf(UIComBack.getInstance().getGroupElevatorId(strParam)),strParamSecond,strInputValue);
								}

							}

						}
					} else {
						String strParamSecond = menu_lift_set_param_select.getButtonEditTextSecond();
						if ((null == strParamSecond) || (strParamSecond.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_select_set_parameters));
						} else {
							String strInputValue = menu_lift_set_param_select.getButtonEditInputText();
							if ((null == strInputValue) || (strInputValue.length() < 1)) {
								TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_lift_tips_set_value_canont_empty));
							} else {
								showSetConfirm(CMD_SET_PARAMETERS,"",strParamSecond,strInputValue);
							}
						}
					}
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_set_param_select.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else if (ButtonEditSelectD.BUTTON_ID_SELECT_SECOND == buttonId) {
					showSelectDialog(-1,getString(R.string.background_lift_tips_select_set_parameters),menu_lift_set_param_select.getButtonEditSecond(), "", TcnConstantLift.LIFT_QUERY_PARAM);
				}
				else {

				}
			} else if (R.id.menu_lift_set_slot_spring == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_slot_spring.setButtonDisplayText("");
					String strParam = menu_lift_set_slot_spring.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						showSetConfirm(CMD_SET_SLOTNO_SPRING,"",strParam,"");
					}
				}
			} else if (R.id.menu_lift_set_slot_belts == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_slot_belts.setButtonDisplayText("");
					String strParam = menu_lift_set_slot_belts.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						showSetConfirm(CMD_SET_SLOTNO_BELTS,"",strParam,"");
					}
				}
			} else if (R.id.menu_lift_set_slot_spring_all == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_slot_spring_all.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_set_slot_spring_all.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							showSetConfirm(CMD_SET_SLOTNO_ALL_SPRING,String.valueOf(UIComBack.getInstance().getGroupElevatorId(strParam)),"","");
						}
					} else {
						showSetConfirm(CMD_SET_SLOTNO_ALL_SPRING,"","","");
					}

				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_set_slot_spring_all.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_set_slot_belts_all == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_slot_belts_all.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_set_slot_belts_all.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							showSetConfirm(CMD_SET_SLOTNO_ALL_BELT,String.valueOf(UIComBack.getInstance().getGroupElevatorId(strParam)),"","");
						}
					} else {
						showSetConfirm(CMD_SET_SLOTNO_ALL_BELT,"","","");
					}

				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_set_slot_belts_all.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_set_single_slot == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_single_slot.setButtonDisplayText("");
					String strParam = menu_lift_set_single_slot.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						showSetConfirm(CMD_SET_SLOTNO_SINGLE,"",strParam,"");
					}

				}
			} else if (R.id.menu_lift_set_double_slot == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_double_slot.setButtonDisplayText("");
					String strParam = menu_lift_set_double_slot.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						showSetConfirm(CMD_SET_SLOTNO_DOUBLE,"",strParam,"");
					}
				}
			} else if (R.id.menu_lift_set_single_slot_all == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_single_slot_all.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_set_single_slot_all.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							showSetConfirm(CMD_SET_SLOTNO_ALL_SINGLE,String.valueOf(UIComBack.getInstance().getGroupElevatorId(strParam)),"","");
						}
					} else {
						showSetConfirm(CMD_SET_SLOTNO_ALL_SINGLE,"","","");
					}

				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_set_single_slot_all.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_query_slot == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					String strParam = menu_lift_query_slot.getButtonEditInputText();
					if ((null == strParam) || (strParam.length() < 1)) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_input_slotno));
					} else {
						TcnVendIF.getInstance().reqQuerySlotStatus(Integer.valueOf(strParam));
					}
				}
			} else if (R.id.menu_lift_set_glass_heat_open == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_glass_heat_open.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_set_glass_heat_open.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							showSetConfirm(CMD_SET_GLASS_HEAT_OPEN,String.valueOf(UIComBack.getInstance().getGroupElevatorId(strParam)),"","");
						}
					} else {
						showSetConfirm(CMD_SET_GLASS_HEAT_OPEN,"","","");
					}

				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_set_glass_heat_open.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			} else if (R.id.menu_lift_set_glass_heat_close == id) {
				if (ButtonEditSelectD.BUTTON_ID_QUERY == buttonId) {
					menu_lift_set_glass_heat_close.setButtonDisplayText("");
					if (UIComBack.getInstance().isMutiGrpElevator()) {
						String strParam = menu_lift_set_glass_heat_close.getButtonEditText();
						if ((null == strParam) || (strParam.length() < 1)) {
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_tips_select_cabinetno));
						} else {
							showSetConfirm(CMD_SET_GLASS_HEAT_CLOSE,String.valueOf(UIComBack.getInstance().getGroupElevatorId(strParam)),"","");
						}
					} else {
						showSetConfirm(CMD_SET_GLASS_HEAT_CLOSE,"","","");
					}

				} else if (ButtonEditSelectD.BUTTON_ID_SELECT == buttonId) {
					showSelectDialog(-1,getString(R.string.background_drive_tips_select_cabinetno),menu_lift_set_glass_heat_close.getButtonEdit(), "",UIComBack.getInstance().getGroupListElevatorShow());
				} else {

				}
			}

			else {

			}
		}
	}


	/*
	 * 此处监听底层发过来的数据，下面是显示相应操作结果
	 */
	private VendListener m_vendListener = new VendListener();
	private class VendListener implements TcnVendIF.VendEventListener {
		@Override
		public void VendEvent(VendEventInfo cEventInfo) {
			if (null == cEventInfo) {
				TcnVendIF.getInstance().LoggerError(TAG, "VendListener cEventInfo is null");
				return;
			}
			switch (cEventInfo.m_iEventID) {
				case TcnVendEventID.COMMAND_SYSTEM_BUSY:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4, 20).show();
					break;

				case TcnVendEventID.SERIAL_PORT_CONFIG_ERROR:
					Log.i(TAG, "SERIAL_PORT_CONFIG_ERROR");
					//TcnUtilityUI.getToast(m_MainActivity, getString(R.string.error_seriport));
					//打开串口错误，一般是串口配置出错
					break;
				case TcnVendEventID.SERIAL_PORT_SECURITY_ERROR:
					///打开串口错误，一般是串口配置出错
					break;
				case TcnVendEventID.SERIAL_PORT_UNKNOWN_ERROR:
					//打开串口错误，一般是串口配置出错
					break;
				case TcnVendEventID.COMMAND_SELECT_GOODS:  //选货成功
					TcnUtilityUI.getToast(MainAct.this, "选货成功");
					break;
				case TcnVendEventID.COMMAND_INVALID_SLOTNO:
					TcnUtilityUI.getToast(MainAct.this, getString(R.string.ui_base_notify_invalid_slot), 22).show();
					break;
				case TcnVendEventID.COMMAND_SOLD_OUT:
					if (cEventInfo.m_lParam1 > 0) {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.ui_base_aisle_name) + cEventInfo.m_lParam1 + getString(R.string.ui_base_notify_sold_out));
					} else {
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.ui_base_notify_sold_out));
					}
					break;
				case TcnVendEventID.COMMAND_FAULT_SLOTNO:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.COMMAND_SHIPPING:    //正在出货
					if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
						if (m_OutDialog == null) {
							m_OutDialog = new OutDialog(MainAct.this, String.valueOf(cEventInfo.m_lParam1), cEventInfo.m_lParam4);
						} else {
							m_OutDialog.setText(cEventInfo.m_lParam4);
						}
						m_OutDialog.cleanData();
					} else {
						if (m_OutDialog == null) {
							m_OutDialog = new OutDialog(MainAct.this, String.valueOf(cEventInfo.m_lParam1), getString(R.string.ui_base_notify_shipping));
						} else {
							m_OutDialog.setText(MainAct.this.getString(R.string.ui_base_notify_shipping));
						}
					}
					m_OutDialog.setNumber(String.valueOf(cEventInfo.m_lParam1));
					m_OutDialog.show();
					break;

				case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:    //出货成功
					if (null != m_OutDialog) {
						m_OutDialog.cancel();
					}
					if (m_LoadingDialog == null) {
						m_LoadingDialog = new LoadingDialog(MainAct.this, getString(R.string.ui_base_notify_shipment_success), getString(R.string.ui_base_notify_receive_goods));
					} else {
						m_LoadingDialog.setLoadText(getString(R.string.ui_base_notify_shipment_success));
						m_LoadingDialog.setTitle(getString(R.string.ui_base_notify_receive_goods));
					}
					m_LoadingDialog.setShowTime(3);
					m_LoadingDialog.show();
					break;
				case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:    //出货失败
					if (null != m_OutDialog) {
						m_OutDialog.cancel();
					}
					if (null == m_LoadingDialog) {
						m_LoadingDialog = new LoadingDialog(MainAct.this, getString(R.string.ui_base_notify_shipment_fail), getString(R.string.ui_base_notify_contact_merchant));
					}
					m_LoadingDialog.setLoadText(getString(R.string.ui_base_notify_shipment_fail));
					m_LoadingDialog.setTitle(getString(R.string.ui_base_notify_contact_merchant));
					m_LoadingDialog.setShowTime(3);
					m_LoadingDialog.show();
					break;
				case TcnVendEventID.CMD_READ_DOOR_STATUS:  //门动作上报
					if (TcnVendEventResultID.DO_CLOSE == cEventInfo.m_lParam1) {   //关门
						TcnUtilityUI.getToast(MainAct.this, "close the door", 20).show();
					} else if (TcnVendEventResultID.DO_OPEN == cEventInfo.m_lParam1) {   //开门
						TcnUtilityUI.getToast(MainAct.this, "open the door", 20).show();
					}
					else {

					}
					break;
				case TcnVendEventID.TEMPERATURE_INFO:   //cEventInfo.m_lParam4  ：温度
//					if (m_main_temperature != null) {
//						m_main_temperature.setText(cEventInfo.m_lParam4);
//					}
					break;
				case TcnVendEventID.PROMPT_INFO:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;

				case TcnVendEventID.COMMAND_QUERY_PARAMETERS:
					selectLiftParam(cEventInfo.m_lParam2);
					break;
				case TcnVendEventID.CMD_QUERY_STATUS_LIFTER:
					if (TcnVendEventResultID.STATUS_FREE == cEventInfo.m_lParam1) {
						menu_lift_query_drive_fault.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.STATUS_BUSY == cEventInfo.m_lParam1) {
						menu_lift_query_drive_fault.setButtonDisplayText(R.string.background_notify_sys_busy);
					} else if (TcnVendEventResultID.STATUS_WAIT_TAKE_GOODS == cEventInfo.m_lParam1) {
						menu_lift_query_drive_fault.setButtonDisplayText(R.string.background_notify_receive_goods);
					} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					}
					else {

					}
					break;
				case TcnVendEventID.CMD_TAKE_GOODS_DOOR:
					if (TcnVendEventResultID.DO_OPEN == cEventInfo.m_lParam3) {
						if (TcnVendEventResultID.DO_START == cEventInfo.m_lParam1) {
							setDialogShow();
							menu_lift_cargo_door_open.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.DO_END == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							menu_lift_cargo_door_open.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
						}
						else {

						}
					} else if (TcnVendEventResultID.DO_CLOSE == cEventInfo.m_lParam3) {
						if (TcnVendEventResultID.DO_START == cEventInfo.m_lParam1) {
							setDialogShow();
							menu_lift_cargo_door_close.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.DO_END == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							menu_lift_cargo_door_close.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
						}
						else {

						}
					} else {

					}
					break;
				case TcnVendEventID.CMD_LIFTER_UP:
					if (TcnVendEventResultID.DO_START == cEventInfo.m_lParam1) {
						setDialogShow();
						menu_lift_up.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.DO_END == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						menu_lift_up.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					}
					else {

					}
					break;
				case TcnVendEventID.CMD_LIFTER_BACK_HOME:
					if (TcnVendEventResultID.DO_START == cEventInfo.m_lParam1) {
						setDialogShow();
						menu_lift_back_home.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.DO_END == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						menu_lift_back_home.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					}
					else {

					}
					break;
				case TcnVendEventID.CMD_CLAPBOARD_SWITCH:
					if (TcnVendEventResultID.DO_OPEN == cEventInfo.m_lParam3) {
						if (TcnVendEventResultID.DO_START == cEventInfo.m_lParam1) {
							setDialogShow();
							menu_lift_clapboard_open.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.DO_END == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							menu_lift_clapboard_open.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
						}
						else {

						}
					} else if (TcnVendEventResultID.DO_CLOSE == cEventInfo.m_lParam3) {
						if (TcnVendEventResultID.DO_START == cEventInfo.m_lParam1) {
							setDialogShow();
							menu_lift_clapboard_close.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.DO_END == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							menu_lift_clapboard_close.setButtonDisplayText(cEventInfo.m_lParam4);
						} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
							if (m_OutDialog != null) {
								m_OutDialog.dismiss();
							}
							TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
						}
						else {

						}
					} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					}
					else {

					}
					break;
				case TcnVendEventID.CMD_OPEN_COOL:
				case TcnVendEventID.CMD_OPEN_HEAT:
				case TcnVendEventID.CMD_CLOSE_COOL_HEAT:
					if (TcnVendEventResultID.DO_START== cEventInfo.m_lParam1) {
						setDialogShow();
						menu_lift_temper_control.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.DO_END == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						menu_lift_temper_control.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					}
					else {

					}
					break;
				case TcnVendEventID.CMD_CLEAN_FAULTS:
					if (TcnVendEventResultID.STATUS_FREE == cEventInfo.m_lParam1) {
						menu_lift_clear_drive_fault.setButtonDisplayText(cEventInfo.m_lParam4);
					} else if (TcnVendEventResultID.STATUS_BUSY == cEventInfo.m_lParam1) {
						menu_lift_clear_drive_fault.setButtonDisplayText(R.string.background_notify_sys_busy);
					} else if (TcnVendEventResultID.STATUS_WAIT_TAKE_GOODS == cEventInfo.m_lParam1) {
						menu_lift_clear_drive_fault.setButtonDisplayText(R.string.background_notify_receive_goods);
					} else if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					}
					else {

					}
					break;
				case TcnVendEventID.CMD_QUERY_PARAMETERS:
					menu_lift_query_param.setButtonDisplayText(String.valueOf(cEventInfo.m_lParam2));
					break;
				case TcnVendEventID.CMD_SET_LIGHT_OUT_STEP:
					if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					}
					else {
						menu_lift_light_steps.setButtonDisplayText(cEventInfo.m_lParam4);
					}
					break;
				case TcnVendEventID.CMD_SET_PARAMETERS:
					if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					} else {
						menu_lift_set_param_select.setButtonDisplayText(cEventInfo.m_lParam4);
					}

					break;
				case TcnVendEventID.CMD_DETECT_LIGHT:
					if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					} else {
						menu_lift_check_light.setButtonDisplayText(cEventInfo.m_lParam4);
					}
					break;
				case TcnVendEventID.CMD_DETECT_SHIP:
					if (TcnVendEventResultID.CMD_NO_DATA_RECIVE == cEventInfo.m_lParam1) {
						if (m_OutDialog != null) {
							m_OutDialog.dismiss();
						}
						TcnUtilityUI.getToast(MainAct.this, getString(R.string.background_drive_check_seriport));
					} else {
						menu_lift_ship_check.setButtonDisplayText(cEventInfo.m_lParam4);
					}
					break;
				case TcnVendEventID.CMD_QUERY_SLOT_STATUS:
					menu_lift_query_slot.setButtonDisplayText(cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.SET_SLOTNO_SPRING:
					menu_lift_set_slot_spring.setButtonDisplayText(cEventInfo.m_lParam4);
					if (m_OutDialog != null) {
						m_OutDialog.dismiss();
					}
					break;
				case TcnVendEventID.SET_SLOTNO_BELTS:
					menu_lift_set_slot_belts.setButtonDisplayText(cEventInfo.m_lParam4);
					if (m_OutDialog != null) {
						m_OutDialog.dismiss();
					}
					break;
				case TcnVendEventID.SET_SLOTNO_ALL_SPRING:
					menu_lift_set_slot_spring_all.setButtonDisplayText(cEventInfo.m_lParam4);
					if (m_OutDialog != null) {
						m_OutDialog.dismiss();
					}
					break;
				case TcnVendEventID.SET_SLOTNO_ALL_BELT:
					menu_lift_set_slot_belts_all.setButtonDisplayText(cEventInfo.m_lParam4);
					if (m_OutDialog != null) {
						m_OutDialog.dismiss();
					}
					break;
				case TcnVendEventID.SET_SLOTNO_SINGLE:
					menu_lift_set_single_slot.setButtonDisplayText(cEventInfo.m_lParam4);
					if (m_OutDialog != null) {
						m_OutDialog.dismiss();
					}
					break;
				case TcnVendEventID.SET_SLOTNO_DOUBLE:
					menu_lift_set_double_slot.setButtonDisplayText(cEventInfo.m_lParam4);
					if (m_OutDialog != null) {
						m_OutDialog.dismiss();
					}
					break;
				case TcnVendEventID.SET_SLOTNO_ALL_SINGLE:
					menu_lift_set_single_slot_all.setButtonDisplayText(cEventInfo.m_lParam4);
					if (m_OutDialog != null) {
						m_OutDialog.dismiss();
					}
					break;
				case TcnVendEventID.CMD_SET_COOL:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.CMD_SET_HEAT:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.CMD_SET_COOL_HEAT_CLOSE:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.CMD_SET_GLASS_HEAT_OPEN:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.CMD_SET_GLASS_HEAT_CLOSE:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.CMD_LIFTER_MOVE_START:
//                    TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				case TcnVendEventID.CMD_LIFTER_MOVE_END:
					TcnUtilityUI.getToast(MainAct.this, cEventInfo.m_lParam4);
					break;
				default:
					break;
			}
		}
	}
}
