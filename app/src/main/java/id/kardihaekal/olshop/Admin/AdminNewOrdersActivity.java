package id.kardihaekal.olshop.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import id.kardihaekal.olshop.Model.AdminOrders;
import id.kardihaekal.olshop.R;
import id.kardihaekal.olshop.Utils.Common;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdminNewOrdersActivity extends AppCompatActivity
{
  private RecyclerView ordersList;
  private DatabaseReference ordersRef;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_new_orders);


    ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");


    ordersList = findViewById(R.id.orders_list);
    ordersList.setLayoutManager(new LinearLayoutManager(this));
  }


  @Override
  protected void onStart()
  {
    super.onStart();


    FirebaseRecyclerOptions<AdminOrders> options =
        new FirebaseRecyclerOptions.Builder<AdminOrders>()
            .setQuery(ordersRef, AdminOrders.class)
            .build();

    FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
        new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
          @Override
          protected void onBindViewHolder(@NonNull final AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
          {
            holder.userName.setText("Nama: " + model.getName());
            holder.userPhoneNumber.setText("Phone: " + model.getPhone());
            holder.userTotalPrice.setText("Total Bayaran =  " + Common.convertToRupiah(model.getTotalAmount()));
            holder.userDateTime.setText("Dipesan pada: " + model.getDate() + "  " + model.getTime());
            holder.userShippingAddress.setText("Alamat Pengiriman: " + model.getAddress() + ", " + model.getCity());
            holder.cardViewNewOrders
                .setCardBackgroundColor(holder.itemView.getResources().getColor(getRandomColour(), null));

            holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view)
              {
                String uID = getRef(position).getKey();

                Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                intent.putExtra("uid", uID);
                startActivity(intent);

              }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view)
              {
                CharSequence options[] = new CharSequence[]
                    {
                        "Ya",
                        "Tidak"
                    };

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                builder.setTitle("Sudahkah anda mengirimkan produk dengan pesanan ini ?");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                    if (i == 0)
                    {
                      String uID = getRef(position).getKey();

                      RemoverOrder(uID);
                    }
                    else
                    {
                      finish();
                    }
                  }
                });
                builder.show();
              }
            });
          }

          @NonNull
          @Override
          public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
          {
            View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
            return new AdminOrdersViewHolder(view);
          }
        };

    ordersList.setAdapter(adapter);
    adapter.startListening();
  }

  private int getRandomColour() {

    List<Integer> colourCode = new ArrayList<>();
    colourCode.add(R.color.red_A400);
    colourCode.add(R.color.pink_A400);
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
    colourCode.add(R.color.deep_orange_A400);
    colourCode.add(R.color.gray_400);
    colourCode.add(R.color.blue_gray_400);

    Random randomColour = new Random();
    int number = randomColour.nextInt(colourCode.size());
    return colourCode.get(number);
  }


  public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
  {
    public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
    public Button ShowOrdersBtn;
    public CardView cardViewNewOrders;


    public AdminOrdersViewHolder(View itemView)
    {
      super(itemView);


      userName = itemView.findViewById(R.id.order_user_name);
      userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
      userTotalPrice = itemView.findViewById(R.id.order_total_price);
      userDateTime = itemView.findViewById(R.id.order_date_time);
      userShippingAddress = itemView.findViewById(R.id.order_address_city);
      ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);
      cardViewNewOrders = itemView.findViewById(R.id.card_view_new_orders);
    }
  }




  private void RemoverOrder(String uID)
  {
    ordersRef.child(uID).removeValue();
  }
}
