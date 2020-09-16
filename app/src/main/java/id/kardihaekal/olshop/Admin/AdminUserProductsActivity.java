package id.kardihaekal.olshop.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import id.kardihaekal.olshop.Model.Cart;
import id.kardihaekal.olshop.R;
import id.kardihaekal.olshop.Utils.Common;
import id.kardihaekal.olshop.ViewHolder.CartViewHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdminUserProductsActivity extends AppCompatActivity
{
  private RecyclerView productsList;
  RecyclerView.LayoutManager layoutManager;
  private DatabaseReference cartListRef;

  private String userID = "";



  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_user_products);


    userID = getIntent().getStringExtra("uid");


    productsList = findViewById(R.id.products_list);
    productsList.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
    productsList.setLayoutManager(layoutManager);


    cartListRef = FirebaseDatabase.getInstance().getReference()
        .child("Cart List").child("Admin View").child(userID).child("Products");
  }


  @Override
  protected void onStart()
  {
    super.onStart();


    FirebaseRecyclerOptions<Cart> options =
        new FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartListRef, Cart.class)
            .build();

    FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
      @Override
      protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model)
      {
        holder.txtProductQuantity.setText("Jumlah = " + model.getQuantity());
        holder.txtProductPrice.setText(Common.convertToRupiah(model.getPrice()));
        holder.txtProductName.setText(model.getPname());
        holder.cardViewItems.setCardBackgroundColor(holder.itemView.getResources().getColor(getRandomColour(), null));

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

    productsList.setAdapter(adapter);
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
}

