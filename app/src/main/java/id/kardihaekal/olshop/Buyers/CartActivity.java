package id.kardihaekal.olshop.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import id.kardihaekal.olshop.Model.Cart;
import id.kardihaekal.olshop.Prevalent.Prevalent;
import id.kardihaekal.olshop.R;
import id.kardihaekal.olshop.Utils.Common;
import id.kardihaekal.olshop.ViewHolder.CartViewHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CartActivity extends AppCompatActivity
{
  private RecyclerView recyclerView;
  private RecyclerView.LayoutManager layoutManager;

  private Button NextProcessBtn;
  private TextView txtTotalAmount, txtMsg1;

  private int overTotalPrice = 0;



  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cart);


    recyclerView = findViewById(R.id.cart_list);
    recyclerView.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    NextProcessBtn = (Button) findViewById(R.id.next_btn);
    txtTotalAmount = (TextView) findViewById(R.id.total_price);
    txtMsg1 = (TextView) findViewById(R.id.msg1);


    NextProcessBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view)
      {
        txtTotalAmount.setText("Total Pembayaran = Rp" + String.valueOf(overTotalPrice));

        Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
        intent.putExtra("Total Price", String.valueOf(overTotalPrice));
        startActivity(intent);
        finish();
      }
    });
  }


  @Override
  protected void onStart()
  {
    super.onStart();

    CheckOrderState();


    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

    FirebaseRecyclerOptions<Cart> options =
        new FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"), Cart.class)
            .build();

    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
        = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
      @Override
      protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
      {
        holder.txtProductQuantity.setText("Jumlah = " + model.getQuantity());
        holder.txtProductPrice.setText(Common.convertToRupiah(model.getPrice()));
        holder.txtProductName.setText(model.getPname());
        holder.cardViewItems.setCardBackgroundColor(holder.itemView.getResources().getColor(getRandomColour(), null));

        int oneTyprProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
        overTotalPrice = overTotalPrice + oneTyprProductTPrice;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view)
          {
            CharSequence options[] = new CharSequence[]
                {
                    "Ubah",
                    "Hapus"
                };
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setTitle("Opsi Keranjang:");

            builder.setItems(options, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i)
              {
                if (i == 0)
                {
                  Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                  intent.putExtra("pid", model.getPid());
                  startActivity(intent);
                }
                if (i == 1)
                {
                  cartListRef.child("User View")
                      .child(Prevalent.currentOnlineUser.getPhone())
                      .child("Products")
                      .child(model.getPid())
                      .removeValue()
                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                          if (task.isSuccessful())
                          {
                            Toast.makeText(CartActivity.this, "Barang berhasil dihapus.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                            startActivity(intent);
                          }
                        }
                      });
                }
              }
            });
            builder.show();
          }
        });
      }

      @NonNull
      @Override
      public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
      {
        View view = LayoutInflater
            .from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
        CartViewHolder holder = new CartViewHolder(view);
        return holder;
      }
    };

    recyclerView.setAdapter(adapter);
    adapter.startListening();
  }

  private int getRandomColour() {
    List<Integer> colourCode = new ArrayList<>();
    colourCode.add(R.color.purple_A400);
    colourCode.add(R.color.deep_purple_A400);
    colourCode.add(R.color.indigo_A400);
    colourCode.add(R.color.blue_A400);
    colourCode.add(R.color.light_blue_A400);
    colourCode.add(R.color.cyan_A400);
    colourCode.add(R.color.teal_A400);
    colourCode.add(R.color.green_A400);
    colourCode.add(R.color.amber_A400);
    colourCode.add(R.color.orange_A400);
    colourCode.add(R.color.gray_400);
    colourCode.add(R.color.blue_gray_400);

    Random randomColour = new Random();
    int number = randomColour.nextInt(colourCode.size());
    return colourCode.get(number);
  }


  private void CheckOrderState()
  {
    DatabaseReference ordersRef;
    ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

    ordersRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot)
      {
        if (dataSnapshot.exists())
        {
          String shippingState = dataSnapshot.child("state").getValue().toString();
          String userName = dataSnapshot.child("name").getValue().toString();

          if (shippingState.equals("shipped"))
          {
            txtTotalAmount.setText("Kepada " + userName + "\n pesanan berhasil dikirim.");
            recyclerView.setVisibility(View.GONE);

            txtMsg1.setVisibility(View.VISIBLE);
            txtMsg1.setText("Selamat, pesanan akhir anda telah berhasil dikirim. Anda akan segera menerima pesanan anda.");
            NextProcessBtn.setVisibility(View.GONE);

            Toast.makeText(CartActivity.this, "Anda dapat membeli lebih banyak produk, setelah pesanan akhir anda diterima.", Toast.LENGTH_SHORT).show();
          }
          else if(shippingState.equals("not shipped"))
          {
            txtTotalAmount.setText("Alamat pengiriman = Tidak terkirim");
            recyclerView.setVisibility(View.GONE);

            txtMsg1.setVisibility(View.VISIBLE);
            NextProcessBtn.setVisibility(View.GONE);

            Toast.makeText(CartActivity.this, "Anda dapat membeli lebih banyak produk, setelah pesanan akhir anda diterima.", Toast.LENGTH_SHORT).show();
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }
}

