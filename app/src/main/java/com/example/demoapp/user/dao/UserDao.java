package com.example.demoapp.user.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.demoapp.user.model.User;

//
/**
 * @Doa used for mark the interface for SQL operation, it will injected into database.
 *
 * learn more - https://developer.android.com/training/data-storage/room#dao
 */
@Dao
public interface UserDao {

    /**
     * @Query used to write custom query
     * param are used in query with leading colon eg: ":[parameter_name]"
     */
    @Query("SELECT * FROM user WHERE username=:username AND password=:password")
    User getUser(String username, String password);
    @Query("SELECT * FROM user WHERE username=:username")
    User getUser(String username);

    /**
     * @Insert, @Update, and @Delete are common annotation can used without writing a query
     * @param user - in case of insert/update new user will be created else updated,
     *             in case of delete it will the user from database.
     */
    @Insert
    void insert(User user);
    @Update
    void update(User user);
    @Delete
    void delete(User user);
}
