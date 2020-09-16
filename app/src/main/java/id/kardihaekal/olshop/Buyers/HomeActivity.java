package id.kardihaekal.olshop.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import id.kardihaekal.olshop.Admin.AdminMaintainProductsActivity;
import id.kardihaekal.olshop.MainActivity;
import id.kardihaekal.olshop.Model.Products;
import id.kardihaekal.olshop.Prevalent.Prevalent;
import id.kardihaekal.olshop.R;
import id.kardihaekal.olshop.Utils.Common;
import id.kardihaekal.olshop.ViewHolder.ProductViewHolder;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
{
  private DatabaseReference ProductsRef;
  private RecyclerView recyclerView;
  RecyclerView.LayoutManager layoutManager;

  private String type = "";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);


    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();
    if (bundle != null)
    {
      type = getIntent().getExtras().get("Admin").toString();
    }



    ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


    Paper.init(this);


    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Home");
    setSupportActionBar(toolbar);


    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view)
      {
        if (!type.equals("Admin"))
        {
          Intent intent = new Intent(HomeActivity.this, CartActivity.class);
          startActivity(intent);
        }
      }
    });


    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();


    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    navigationView.bringToFront(); //gunakan ini apabila setNavigationItemCLickListenernya tidak berfungsi

    View headerView = navigationView.getHeaderView(0);
    TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
    CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);


    if (!type.equals("Admin"))
    {
      userNameTextView.setText(Prevalent.currentOnlineUser.getName());
      Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
    }


    recyclerView = findViewById(R.id.recycler_menu);
    recyclerView.setHasFixedSize(true);
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
  }


  @Override
  protected void onStart()
  {
    super.onStart();

    FirebaseRecyclerOptions<Products> options =
        new FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(ProductsRef.orderByChild("productState").equalTo("Approved"), Products.class)
            .build();


    FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
        new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
          @Override
          protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
          {
            holder.txtProductName.setText(model.getPname());
            holder.txtProductDescription.setText(model.getDescription());
            holder.txtProductPrice.setText("Harga = " + Common.convertToRupiah(model.getPrice()));
            Picasso.get().load(model.getImage()).into(holder.imageView);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view)
              {
                if (type.equals("Admin"))
                {
                  Intent intent = new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                  intent.putExtra("pid", model.getPid());
                  startActivity(intent);
                }
                else
                {
                  Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                  intent.putExtra("pid", model.getPid());
                  startActivity(intent);
                }
              }
            });
          }

          @NonNull
          @Override
          public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
          {
            View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
            ProductViewHolder holder = new ProductViewHolder(view);
            return holder;
          }
        };
    recyclerView.setAdapter(adapter);
    adapter.startListening();
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.home, menu);
    return true;
  }



  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

    return super.onOptionsItemSelected(item);
  }



  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item)

  {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_cart)
    {
      if (!type.equals("Admin"))
      {
        Intent intent = new Intent(HomeActivity.this, CartActivity.class);
        startActivity(intent);
      }
    }
    else if (id == R.id.nav_search)
    {
      if (!type.equals("Admin"))
      {
        Intent intent = new Intent(HomeActivity.this, SearchProductsActivity.class);
        startActivity(intent);
      }
    }
    else if (id == R.id.nav_categories)
    {

    }
    else if (id == R.id.nav_settings)
    {
      if (!type.equals("Admin"))
      {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
      }
    }
    else if (id == R.id.nav_logout)
    {
      if (!type.equals("Admin"))
      {
        Paper.book().destroy();

        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
      }
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    drawer.bringToFront(); //gunakan ini apabila setNavigationItemCLickListenernya tidak berfungsi
    drawer.requestLayout(); //gunakan ini apabila setNavigationItemCLickListenernya tidak berfungsi
    return true;
  }
}