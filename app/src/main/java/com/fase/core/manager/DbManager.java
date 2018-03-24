package com.fase.core.manager;

import com.fase.BuildConfig;
import com.fase.core.RxRealm;
import com.fase.model.UpdateValue;
import com.fase.model.db.ResourceDbModel;
import com.fase.model.db.UpdateValueDbModel;
import com.fase.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class DbManager {

    private static volatile DbManager mSingleton;

    public static synchronized DbManager getInstance() {
        if (mSingleton == null) {
            synchronized (DbManager.class) {
                if (mSingleton == null) {
                    mSingleton = new DbManager();
                }
            }
        }
        return mSingleton;
    }

    private DbManager() {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder()
                .name(BuildConfig.DB_NAME)
                .schemaVersion(BuildConfig.DB_VERSION);

        if (BuildConfig.DEBUG) {
            builder = builder.deleteRealmIfMigrationNeeded();
        } else {
            builder = builder.deleteRealmIfMigrationNeeded();
//            builder.migration(new RealmDbMigration()); // TODO: implement migration if needed
        }

        RealmConfiguration configuration = builder.build();
        Realm.setDefaultConfiguration(configuration);
    }

    /**
     * Methods
     */

    /* Resources */
    Single<ArrayList<String>> getResourcesFilePathList() {
        return RxRealm.getList(realm -> realm.where(ResourceDbModel.class).findAll())
                .map(resourceDbModels -> {
                    ArrayList<String> filePathList = new ArrayList<>();
                    if (resourceDbModels != null && !resourceDbModels.isEmpty()) {
                        for (ResourceDbModel resourceDbModel : resourceDbModels) {
                            if (resourceDbModel != null) {
                                filePathList.add(resourceDbModel.getFilePath());
                            }
                        }
                    }
                    return filePathList;
                }).toSingle();
    }

    Maybe<String> getResourcePath(String fileName) {
        return RxRealm.getElement(realm -> realm.where(ResourceDbModel.class).equalTo("fileName", fileName).findFirst())
                .flatMap(resourceDbModel -> Maybe.just(resourceDbModel.getFilePath()));
    }

    Completable putResourceToDb(String fileName, String filePath) {
        return RxRealm.doTransactional(realm -> realm.copyToRealmOrUpdate(new ResourceDbModel(fileName, filePath)));
    }

    Single<Boolean> isResourceExist(String fileName) {
        return RxRealm.isElementExist(realm -> realm.where(ResourceDbModel.class).equalTo("fileName", fileName).findFirst());
    }

    Single<Boolean> deleteResources() {
        return RxUtil.toSingle(RxRealm.doTransactional(realm -> realm.delete(ResourceDbModel.class)));
    }

    /* Updates */
    Single<Boolean> putValueUpdate(String elementId, List<String> idList, String value) {
        RealmList<String> idListRealm = new RealmList<>();
        idListRealm.addAll(idList);
        return RxUtil.toSingle(RxRealm.doTransactional(realm -> realm.copyToRealmOrUpdate(new UpdateValueDbModel(elementId, idListRealm, value))));
    }

    Single<Boolean> deleteIfEquals(String elementId, String value) {
        return RxUtil.toSingle(RxRealm.doTransactional(realm -> realm.where(UpdateValueDbModel.class).equalTo("elementId", elementId).and().equalTo("value", value).findAll().deleteAllFromRealm()));
    }

    Single<List<UpdateValue>> getUpdateValues() {
        return RxRealm.getList(realm -> realm.where(UpdateValueDbModel.class).findAll())
                .toObservable()
                .flatMap(Observable::fromIterable)
                .map(updateValueDbModel -> {
                    List<String> idList = new ArrayList<>(updateValueDbModel.getIdList());
                    return new UpdateValue(updateValueDbModel.getElementId(), idList, updateValueDbModel.getValue());
                })
                .toList();
    }

    Completable clearValueUpdates() {
        return RxRealm.doTransactional(realm -> realm.where(UpdateValueDbModel.class).findAll().deleteAllFromRealm());
    }
}

