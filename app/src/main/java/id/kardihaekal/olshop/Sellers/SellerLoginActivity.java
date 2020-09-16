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
import id.kardihaekal.olshop.R;

public class SellerLoginActivity extends AppCompatActivity {

  private Button loginSellerBtn;
  private EditText emailInput, passwordInput;
  private ProgressDialog loadingBar;

  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_seller_login);

    emailInput = findViewById(R.id.seller_login_email);
    passwordInput = findViewById(R.id.seller_login_password);
    loginSellerBtn = findViewById(R.id.seller_login_btn);
    loadingBar = new ProgressDialog(this);

    mAuth = FirebaseAuth.getInstance();

    loginSellerBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        loginSeller();
      }
    });
  }

  private void loginSeller() {
    final String email = emailInput.getText().toString();
    final String password = passwordInput.getText().toString();

    if (!email.equals("") && !password.equals("")) {

      loadingBar.setTitle("Masuk ke akun penjual");
      loadingBar.setMessage("Mohon tunggu, sementara kami memeriksa kredensial.");
      loadingBar.setCanceledOnTouchOutside(false);
      loadingBar.show();

      mAuth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful()) {
                Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
              }
            }
          });

    } else {
      Toast.makeText(this, "Silahkan lengkapi form masuk", Toast.LENGTH_SHORT).show();

    }
  }
}
