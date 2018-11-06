package com.aaronhalbert.nosurfforreddit.dependencyinjection.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aaronhalbert.nosurfforreddit.network.NoSurfRepository;
import com.aaronhalbert.nosurfforreddit.room.ClickedPostIdDao;
import com.aaronhalbert.nosurfforreddit.room.ClickedPostIdRoomDatabase;
import com.aaronhalbert.nosurfforreddit.room.InsertClickedPostIdThreadPoolExecutor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ApplicationModule {
     private final Application application;

     public ApplicationModule(Application application) {
         this.application = application;
     }

    @Singleton
    @Provides
    NoSurfRepository provideNoSurfRepository(Retrofit retrofit,
                                             @Named("oAuthSharedPrefs") SharedPreferences preferences,
                                             ClickedPostIdRoomDatabase db,
                                             InsertClickedPostIdThreadPoolExecutor executor) {
        return new NoSurfRepository(retrofit, preferences, db, executor);
    }

    @Singleton
    @Provides
    Application provideApplication() {
        return application;
    }

    @Singleton
    @Provides
    @Named("oAuthSharedPrefs")
        SharedPreferences provideOAuthSharedPrefs() {
        return application.getSharedPreferences(application.getPackageName() + "oauth", Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    @Named("defaultSharedPrefs")
    SharedPreferences provideDefaultSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Singleton
    @Provides
    ClickedPostIdRoomDatabase provideClickedPostIdRoomDatabase() {
        return ClickedPostIdRoomDatabase.getDatabase(application);
    }

    @Singleton
    @Provides
    InsertClickedPostIdThreadPoolExecutor provideInsertClickedPostIdThreadPoolExecutor() {
         //TODO: should this class be converted to use a regular public constructor?
        return InsertClickedPostIdThreadPoolExecutor.getInstance();
    }
}
