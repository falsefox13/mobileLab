package com.example.mobilelab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final int IMAGE_REQUEST = 1;
    private FloatingActionButton uploadImgButton;
    private ImageView profileImage;
    private Button newEmailButton;
    private Button newNameButton;
    private EditText newEmail;
    private EditText newName;
    private TextView name;
    private TextView email;
    private FirebaseUser fuser;
    private StorageReference storageReference;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_profile, container, false);
        setupViews(inflate);
        uploadImgButton.setOnClickListener(view -> openImage());
        newNameButton.setOnClickListener(view -> updateName());
        newEmailButton.setOnClickListener(view -> showEmailUpdateDialog());
        return inflate;
    }

    private void setupViews(View inflate) {
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        uploadImgButton = inflate.findViewById(R.id.new_image_button);
        newEmailButton = inflate.findViewById(R.id.new_email_button);
        newNameButton = inflate.findViewById(R.id.new_name_button);
        newName = inflate.findViewById(R.id.name_new);
        newEmail = inflate.findViewById(R.id.email_new);
        profileImage = inflate.findViewById(R.id.image_profile);
        name = inflate.findViewById(R.id.username);
        email = inflate.findViewById(R.id.email);
        initOnRefresh(inflate.findViewById(R.id.pullToRefresh));
        showUserDetails();
    }

    private void initOnRefresh(SwipeRefreshLayout pullToRefresh) {
        pullToRefresh.setOnRefreshListener(() -> {
            showUserDetails();
            pullToRefresh.setRefreshing(false);
        });
    }

    private void showUserDetails() {
        if (fuser != null) {
            name.setText(fuser.getDisplayName());
            email.setText(fuser.getEmail());
            showProfileImage(Objects.requireNonNull(fuser.getPhotoUrl()).toString());
        }
    }

    private void reauthenticate(final String password) {
        final AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(fuser.getEmail()), password);
        fuser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
            }
        });
    }

    private void updateEmail() {
        final String email = newEmail.getText().toString();
        if (fuser != null && Utils.validateEmail(email)) {
            performEmailUpdate(email);
        } else {
            newEmail.setError(getString(R.string.email_error));
        }
    }

    private void performEmailUpdate(String email) {
        newEmail.setError(null);
        this.email.setText(email);
        fuser.updateEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), R.string.email_success, Toast.LENGTH_SHORT).show();
                newEmail.getText().clear();
            } else {
                Toast.makeText(getContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmailUpdateDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        final EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        dialog.setTitle(getString(R.string.confirm_title));
        dialog.setView(editText);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.confirm),
                (dialogInterface, i) -> {
                    reauthenticate(editText.getText().toString());
                    updateEmail();
                });
        dialog.show();
    }

    private void updateName() {
        final String name = newName.getText().toString();
        if (Utils.validateString(name)) {
            performNameUpdate(name);
        } else {
            newName.setError(getString(R.string.name_error));
        }
    }

    private void performNameUpdate(String name) {
        newName.setError(null);
        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
        this.name.setText(name);
        fuser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.name_success, Toast.LENGTH_SHORT).show();
                        newName.getText().clear();
                    }
                });
    }

    private void showProfileImage(final String photoUrl) {
        final int TARGET_WIDTH = 200;
        final int TARGET_HEIGHT = 200;

        Picasso.get()
                .load(photoUrl)
                .resize(TARGET_WIDTH, TARGET_HEIGHT)
                .centerCrop()
                .into(profileImage);
    }

    private void openImage() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), R.string.upload_progress, Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = Objects.requireNonNull(getContext()).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.uploading));
        progressDialog.show();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(fuser.getUid()
                    + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updatePhoto(task.getResult());
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void updatePhoto(Uri newUri) {
        if (newUri != null) {
            final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(newUri).build();
            fuser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.image_success, Toast.LENGTH_SHORT).show();
                        }
                    });
            showProfileImage(newUri.toString());
        }
    }
}