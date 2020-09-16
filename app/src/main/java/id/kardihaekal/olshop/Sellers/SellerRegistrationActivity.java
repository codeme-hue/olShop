package id.kardihaekal.olshop.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import id.kardihaekal.olshop.Buyers.LoginActivity;
import id.kardihaekal.olshop.MainActivity;
import id.kardihaekal.olshop.R;
import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

  private Button sellerLoginBegin;
  private EditText nameInput, phoneInput, emailInput, passwordInput, addressInput;
  private Button registerButton;
  private ProgressDialog loadingBar;

  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_seller_registration);

    mAuth = FirebaseAuth.getInstance();
    loadingBar = new ProgressDialog(this);

    sellerLoginBegin = findViewById(R.id.seller_already_have_account_btn);
    registerButton = findViewById(R.id.seller_register_btn);
    nameInput = findViewById(R.id.seller_name);
    phoneInput = findViewById(R.id.seller_phone);
    emailInput = findViewById(R.id.seller_email);
    passwordInput = findViewById(R.id.seller_password);
    addressInput = findViewById(R.id.seller_addres);

    sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view)
      {
        Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
        startActivity(intent);
      }
    });

    registerButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        registerSeller();

      }
    });

  }

  private void registerSeller() {

     final String name = nameInput.getText().toString();
     final String phone = phoneInput.getText().toString();
     final String email = emailInput.getText().toString();
     String password = passwordInput.getText().toString();
     final String address = addressInput.getText().toString();

     if (passwordInput.length()<6) {

       passwordInput.setError("Password harus lebih dari 6 karakter");

     }

    if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals(""))
    {

      loadingBar.setTitle("Membuat akun penjual");
      loadingBar.setMessage("Mohon tunggu, sementara kami memeriksa kredensial.");
      loadingBar.setCanceledOnTouchOutside(false);
      loadingBar.show();

      mAuth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

              if (task.isSuccessful())
              {
                final DatabaseReference rootRef;
                rootRef = FirebaseDatabase.getInstance().getReference();

                String sid = mAuth.getCurrentUser().getUid();

                HashMap<String, Object> sellerMap = new HashMap<>();
                sellerMap.put("sid", sid);
                sellerMap.put("phone", phone);
                sellerMap.put("email", email);
                sellerMap.put("address", address);
                sellerMap.put("name", name);

                rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {

                        loadingBar.dismiss();
                        Toast.makeText(SellerRegistrationActivity.this,
                            "Pendaftaran akun telah berhasil", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                      }
                    });
              }

            }
          });
    }
    else
    {
      Toast.makeText(SellerRegistrationActivity.this,
          "Mohon lengkapi form pendaftaran", Toast.LENGTH_SHORT).show();
    }

  }
}
