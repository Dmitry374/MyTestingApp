package com.example.dima.mytestingapp.api;

import com.example.dima.mytestingapp.Items.ItemServerData;
import com.example.dima.mytestingapp.Items.ItemServerGet;
import com.example.dima.mytestingapp.Items.ItemServerReminder;
import com.example.dima.mytestingapp.Items.ItemServerSpend;
import com.example.dima.mytestingapp.Items.ItemServerStatistic;
import com.example.dima.mytestingapp.Items.ItemServerTotal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Dima on 29.08.2017.
 */

public interface ServerApi {

    @GET("/server_email_exist.php/")
    Call<String> countEmail(
            //@Query("action_get") String action,
            //@Query("table_name_get") String tableName,
            //@Query("select_field_by") String fieldBy,
            @Query("email_get") String email
    );

    @GET("/server_login_exist.php")
    Call<String> countLogin(
            //@Query("action_get") String action,
            //@Query("table_name_get") String tableName,
            //@Query("select_field_by") String fieldBy,
            @Query("login_get") String login
    );

    @FormUrlEncoded
    @POST("/server_register_post_inf.php/")
    Call<List<ItemServerData>> saveDataUser(
            @Field("name_post") String name,
            @Field("surname_post") String surname,
            @Field("patronymic_post") String patronymic,
            @Field("gender_post") String gender,
            @Field("date_of_birth_post") String date_of_birth,
            @Field("mobile_phone_post") String mobile_phine,
            @Field("email_post") String email,
            @Field("login_post") String login,
            @Field("password_post") String password,
            @Field("key_sign_post") String key_sign
    );

    @FormUrlEncoded
    @POST("/server_logining_inf.php/")
    Call<List<ItemServerData>> loginUser(
            @Field("login_post") String login,
            @Field("password_post") String password
    );

    @GET("/server_email_exist.php/")
    Call<String> selectIsSign(
            @Query("login_get") String login
    );
//    @FormUrlEncoded
//    @POST("/server_sign_in_select.php/")
//    Call<String> selectIsSign(
//            @Field("login_post") String login
//    );

    @FormUrlEncoded
    @POST("/server_sign_in_upd.php/")
    Call<Void> updateIsSign(
            @Field("login_post") String login,
            @Field("key_sign_post") String keySign
    );

    @FormUrlEncoded
    @POST("/server_write_table.php/")
    Call<Void> writeGeneralTable(
            @Field("table_name") String tableName,
            @Field("name") String name,
            @Field("image_id") String imgId,
            @Field("kol") String kol,
            @Field("ref_login") String refLogin,
            @Field("local_id") String localId
    );

    @FormUrlEncoded
    @POST("/server_write_total.php/")
    Call<Void> writeTotalGetTable(
            @Field("kol_total_get") String kolTotalGet,
            @Field("ref_login_total_get") String refLoginTotalGet
    );

    @FormUrlEncoded
    @POST("/server_add_new_btn.php/")
    Call<Void> addNewButton(
            @Field("table_name") String tableName,
            @Field("name") String name,
            @Field("image_id") String imgId,
            @Field("kol") String kol,
            @Field("ref_login") String refLogin,
            @Field("local_id") String localId
    );

    @FormUrlEncoded
    @POST("/server_edit_btn.php/")
    Call<Void> editButton(
            @Field("table_name") String tableName,
            @Field("new_btn_name") String newBtnName,
            @Field("image_id") String imgId,
            @Field("btn_name") String btnName,
            @Field("ref_login") String refLogin
    );

    @FormUrlEncoded
    @POST("/server_edit_kol.php/")
    Call<Void> setKolBtn(
            @Field("table_name") String tableName,
            @Field("count") String count,
            @Field("ref_login") String refLogin,
            @Field("btn_name") String btnName,
            @Field("count_total") String countTotal,
            @Field("get_count") String getCount,
            @Field("get_name") String getName
    );

    @FormUrlEncoded
    @POST("/server_add_statistic.php/")
    Call<Void> addStatistic(
            @Field("type_statistic") String typeStatistic,
            @Field("name_statistic_get") String nameStatisticGet,
            @Field("kol_statistic") String kolStatistic,
            @Field("name_statistic_spend") String nameStatisticSpend,
            @Field("date_statistic") String dateStatistic,
            @Field("time_statistic") String timeStatistic,
            @Field("reflogin_statistic") String refLoginStatistic,
            @Field("local_id") String localId
    );

    @FormUrlEncoded
    @POST("/server_add_reminder.php/")
    Call<Void> addReminder(
            @Field("reminder_type") String reminderType,
            @Field("reminder_name") String reminderName,
            @Field("reminder_date") String reminderDate,
            @Field("reminder_time") String reminderTime,
            @Field("reminder_repeat") String reminderRepeat,
            @Field("reminder_img_marker") String reminderImgMarker,
            @Field("reminder_marker_name") String reminderMarkerName,
            @Field("reminder_sound") String reminderSound,
            @Field("reminder_reflogin") String reminderRefLogin,
            @Field("local_id") String reminderLocalId
    );

    @FormUrlEncoded
    @POST("/server_edit_transaction.php/")
    Call<Void> editTransaction(
            @Field("kol_get") String kolGet,
            @Field("ref_login") String refLogin,
            @Field("name_get") String nameGet,
            @Field("kol") String kol,
            @Field("btn_name") String btnName
    );

    @FormUrlEncoded
    @POST("/server_edit_statistic.php/")
    Call<Void> editStatistic(
            @Field("new_statistic_name_spend") String newStatisticNameSpend,
            @Field("ref_login") String refLogin,
            @Field("name_statistic_spend") String nameStatisticSpend,
            @Field("statistic_type") String type
    );

    @FormUrlEncoded
    @POST("/server_ed_last_record.php/")
    Call<Void> editLastRecord(
            @Field("type") String type,
            @Field("new_sum") String newSum,
            @Field("ref_login") String refLogin,
            @Field("kol_get") String kolGet,
            @Field("btn_name") String btnName,
            @Field("kol_total") String kolTotal
    );

    @FormUrlEncoded
    @POST("/server_ed_last_record_spend.php/")
    Call<Void> editLastRecordSpend(
            @Field("btn_name") String btnName,
            @Field("kol_get") String kolGet,
            @Field("ref_login") String refLogin,
            @Field("get_name") String getName,
            @Field("kol_get2") String kolGet2,
            @Field("get_name2") String getName2,
            @Field("statistic_kol") String statisticKol,
            @Field("kol_spend") String kolSpend
    );

    @FormUrlEncoded
    @POST("/server_edit_reminder.php/")
    Call<Void> editReminder(
            @Field("reminder_type") String reminderType,
            @Field("reminder_name") String reminderName,
            @Field("reminder_date") String reminderDate,
            @Field("reminder_time") String reminderTime,
            @Field("reminder_repeat") String reminderRepeat,
            @Field("reminder_img_marker") String reminderImageMarker,
            @Field("reminder_marker_name") String reminderMarkerName,
            @Field("reminder_sound") String reminderSound,
            @Field("reminder_reflogin") String reminderRefLogin,
            @Field("local_id") String reminderLocalId
    );

    @FormUrlEncoded
    @POST("/server_edit_reminder_type.php/")
    Call<Void> editReminderType(
            @Field("reminder_type") String reminderType,
            @Field("local_id") String reminderLocalId,
            @Field("reminder_reflogin") String reminderRefLogin,
            @Field("reminder_repeat") String reminderRepeat
    );

    @FormUrlEncoded
    @POST("/server_edit_reminder_not_one_time.php/")
    Call<Void> editReminderRepeatNotOnes(
            @Field("reminder_type") String reminderType,
            @Field("reminder_date") String reminderDate,
            @Field("reminder_time") String reminderTime,
            @Field("reminder_img_marker") String reminderImageMarker,
            @Field("reminder_marker_name") String reminderMarkerName,
            @Field("reminder_sound") String reminderSound,
            @Field("local_id") String reminderLocalId,
            @Field("reminder_reflogin") String reminderRefLogin,
            @Field("reminder_repeat") String reminderRepeat
    );

    @FormUrlEncoded
    @POST("/server_edit_reminder_done_all.php/")
    Call<Void> editReminderDoneAll(
            @Field("group_name") String groupName,
            @Field("reminder_reflogin") String reminderRefLogin
    );

    @FormUrlEncoded
    @POST("/server_lessening_total.php/")
    Call<Void> lesseningTotal(
            @Field("kol_total_get") String kolTotalGet,
            @Field("ref_login") String refLogin
    );

    @FormUrlEncoded
    @POST("/server_delete_from_table_get_sp.php/")
    Call<Void> deleteFromTableGet(
            @Field("type") String type,
            @Field("btn_name") String btnName,
            @Field("ref_login") String refLogin
    );

    @FormUrlEncoded
    @POST("/server_delete_from_reminders.php/")
    Call<Void> deleteFromReminders(
            @Field("type_del") String typeDel,
            @Field("reminder_type") String reminderType,
            @Field("reminder_reflogin") String refLogin,
            @Field("local_id") String localId
    );

    @FormUrlEncoded
    @POST("/server_edit_zeroing.php/")
    Call<Void> editZeroing(
            @Field("ref_login") String login
    );


    @FormUrlEncoded
    @POST("/server_load_get.php/")
    Call<List<ItemServerGet>> loadTableGet(
            @Field("login") String login
    );

//    @GET("/server_load_get.php/")
//    Call<List<ItemServerGet>> loadTableGet(
//            @Query("login") String login
//    );

    @FormUrlEncoded
    @POST("/server_load_spend.php/")
    Call<List<ItemServerSpend>> loadTableSpend(
            @Field("login") String login
    );

    @FormUrlEncoded
    @POST("/server_load_statistic.php/")
    Call<List<ItemServerStatistic>> loadTableStatistic(
            @Field("login") String login
    );

    @FormUrlEncoded
    @POST("/server_load_total.php/")
    Call<List<ItemServerTotal>> loadTableTotal(
            @Field("login") String login
    );

    @FormUrlEncoded
    @POST("/server_load_reminders.php/")
    Call<List<ItemServerReminder>> loadReminders(
            @Field("reminder_reflogin") String login
    );


//    --------- Synchronise --------------

    @FormUrlEncoded
    @POST("/server_update_user.php/")
    Call<Void> updateUserTable(
            @Field("name_user") String userName,
            @Field("surname_user") String userSurname,
            @Field("patronymic_user") String userPatronymic,
            @Field("gender_user") String userGender,
            @Field("date_of_birth_user") String userDateOfBirth,
            @Field("mobile_user") String userMobile,
            @Field("email_user") String userEmail,
            @Field("login_user") String userLogin,
            @Field("password_user") String userPassword
    );

    @FormUrlEncoded
    @POST("/server_update_get.php/")
    Call<Void> updateGetTable(
            @Field("action") String action,
            @Field("name_get") String getName,
            @Field("image_get") String getImage,
            @Field("kol_get") String getKol,
            @Field("ref_login_get") String getRefLogin,
            @Field("local_id") String localId
    );

    @FormUrlEncoded
    @POST("/server_update_spend.php/")
    Call<Void> updateSpendTable(
            @Field("action") String action,
            @Field("name") String spendName,
            @Field("image") String spendImage,
            @Field("kol") String spendKol,
            @Field("ref_login") String spendRefLogin,
            @Field("local_id") String localId
    );

    @FormUrlEncoded
    @POST("/server_update_total.php/")
    Call<Void> updateTotalTable(
            @Field("kol") String kol,
            @Field("ref_login") String refLogin
    );

    @FormUrlEncoded
    @POST("/server_update_statistic.php/")
    Call<Void> updateStatisticTable(
            @Field("action") String action,
            @Field("type_statistic") String type,
            @Field("name_statistic_get") String nameGet,
            @Field("kol_statistic") String kol,
            @Field("name_statistic_spend") String nameSpend,
            @Field("date_statistic") String date,
            @Field("time_statistic") String time,
            @Field("reflogin_statistic") String refLogin,
            @Field("local_id") String localId
    );

}
