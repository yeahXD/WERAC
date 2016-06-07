package kr.werac.yeah.werac_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.werac.yeah.R;
import kr.werac.yeah.data.Comment;

/**
 * Created by Tacademy on 2016-05-19.
 */
public class DetailCommentListHolder extends RecyclerView.ViewHolder {

    TextView tv_cmt_writer;
    CircleImageView image_cmt_writer;
    TextView tv_cmt_content;
    TextView text_cmmt_like;
    ListView lv_recomment;

    public interface OnCommentItemClickListener {
        void onItemClick(View view, TextView tv_cmt_writer);
    }

    OnCommentItemClickListener mListener;
    public void setOnCommentItemClickListener(OnCommentItemClickListener listener) {
        mListener = listener;
    }

    public DetailCommentListHolder(View itemView) {
        super(itemView);
        tv_cmt_writer = (TextView)itemView.findViewById(R.id.tv_rcmt_writer);
        image_cmt_writer = (CircleImageView)itemView.findViewById(R.id.image_rcmt_writer);
        tv_cmt_content = (TextView)itemView.findViewById(R.id.tv_rcmt_content);
        text_cmmt_like = (TextView)itemView.findViewById(R.id.text_cmmt_like);
    }

    public void setmCmt_item(Comment cmt_item){
        tv_cmt_writer.setText(""+cmt_item.getUser().getName());
        if(cmt_item.getUser().getProfile_image() == null)
            image_cmt_writer.setImageResource(R.drawable.profile_default);
        else
            Glide.with(image_cmt_writer.getContext()).load(cmt_item.getUser().getProfile_image()).into(image_cmt_writer);
        tv_cmt_content.setText(cmt_item.getContent());
        text_cmmt_like.setText(cmt_item.getLike()+"");
    }
}
