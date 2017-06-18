package com.github.abhijitpparate.keeper.data.storage;


import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class FirebaseStorageService implements StorageSource {

    public static FirebaseStorageService getInstance() {
        return new FirebaseStorageService();
    }

    @Override
    public Maybe<File> getFile(final String uid, final File file) {
        return Maybe.create(
                new MaybeOnSubscribe<File>() {
                    @Override
                    public void subscribe(final MaybeEmitter<File> emitter) throws Exception {
                        StorageReference root
                                = FirebaseStorage.getInstance().getReference();

                        StorageReference fileRef = root.child(uid).child(file.getType()).child(file.getName());

                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                file.setUrl(uri.toString());
                                emitter.onSuccess(file);
                                emitter.onComplete();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
                    }
                }
        );
    }

    @Override
    public Maybe<File> createFile(final String uid, final String name, final String type, final Uri uri) {
        return Maybe.create(
                new MaybeOnSubscribe<File>() {
                    @Override
                    public void subscribe(final MaybeEmitter<File> emitter) throws Exception {
                        StorageReference root
                                = FirebaseStorage.getInstance().getReference().child(uid).child(type.toUpperCase()).child(name);

                        UploadTask uploadTask = root.putFile(uri);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(new Exception(e.getMessage()));
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                File file = new File();
                                file.setName(taskSnapshot.getMetadata().getName());
                                file.setType(taskSnapshot.getMetadata().getContentType());
                                file.setUrl(taskSnapshot.getMetadata().getDownloadUrl().toString());
                                emitter.onSuccess(file);
                                emitter.onComplete();
                            }
                        });
                    }
                }
        );
    }

    @Override
    public Completable deleteFile(final String uid, final File file) {
        return Completable.create(
                new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter emitter) throws Exception {
                        StorageReference root
                                = FirebaseStorage
                                .getInstance().getReference()
                                .child(uid)
                                .child(String.valueOf(file.getType()))
                                .child(file.getName());

                        root.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                emitter.onComplete();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                emitter.onError(e);
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void setReturnFail(boolean bool) {

    }
}
