package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.naver.maps.map.overlay.InfoWindow;
import com.caucap2021_1_2_10.ddubuk2.R;

public class pointAdapter extends InfoWindow.DefaultViewAdapter
{
    private final Context mContext;
    private final ViewGroup mParent;

    public pointAdapter(@NonNull Context context, ViewGroup parent)
    {
        super(context);
        mContext = context;
        mParent = parent;
    }

    @NonNull
    @Override
    protected View getContentView(@NonNull InfoWindow infoWindow)
    {

        View view = (View) LayoutInflater.from(mContext).inflate(R.layout.item_point, mParent, false);

        TextView txtTitle = (TextView) view.findViewById(R.id.txttitle);
        ImageView imagePoint = (ImageView) view.findViewById(R.id.imagepoint);
        TextView txtAddr = (TextView) view.findViewById(R.id.txtaddr);
        TextView txtTel = (TextView) view.findViewById(R.id.txttel);

        txtTitle.setText("가게명");
        imagePoint.setImageResource(R.drawable.ic_launcher_background);
        txtAddr.setText("제주 제주시 문연로 6\n(지번) 연동 312-1");
        txtTel.setText("064-710-2114");

        return view;
    }
}