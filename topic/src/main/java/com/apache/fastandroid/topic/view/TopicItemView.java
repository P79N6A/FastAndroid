package com.apache.fastandroid.topic.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apache.fastandroid.artemis.support.bean.User;
import com.apache.fastandroid.topic.R;
import com.apache.fastandroid.topic.TopicDetailActivity;
import com.apache.fastandroid.topic.bean.TopicBean;
import com.tesla.framework.common.util.date.FormatTimeUtil;
import com.tesla.framework.component.imageloader.ImageLoaderManager;
import com.tesla.framework.ui.fragment.itemview.ARecycleViewItemViewHolder;

/**
 * Created by 01370340 on 2017/9/3.
 */

public class TopicItemView  extends ARecycleViewItemViewHolder<TopicBean> {
    public static final int LAYOUT_RES = R.layout.topic_item_topic;

    TextView username;
    TextView node_name;
    TextView time;
    TextView title;
    TextView state;
    ImageView avatar;

    public TopicItemView(Activity context, View itemView) {
        super(context, itemView);

        username = (TextView) itemView.findViewById(R.id.username);
        node_name = (TextView) itemView.findViewById(R.id.node_name);
        time = (TextView) itemView.findViewById(R.id.time);
        title = (TextView) itemView.findViewById(R.id.title);
        state = (TextView) itemView.findViewById(R.id.state);
        avatar = (ImageView) itemView.findViewById(R.id.avatar);
    }

    @Override
    public void onBindData(View convertView, final TopicBean bean, int position) {
        final User user = bean.user;
        username.setText(user.getLogin());
        node_name.setText(bean.node_name);
        time.setText(FormatTimeUtil.computePastTime(bean.updated_at));
        title.setText(bean.title);
        state.setText("评论 " + bean.replies_count);


        String url = user.getAvatar_url();
        if (!TextUtils.isEmpty(url) && url.contains("diycode")) {   // 添加判断，防止替换掉其他网站掉图片
            url = url.replace("large_avatar", "avatar");
        }
        //Glide.with(getContext()).load(url).into(avatar);
        final String finalUrl = url;
        /*Glide.with(getContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                    isFirstResource) {
                NLog.d(NLog.TAG, "onException url = %s, Exception = %s", finalUrl,e);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(avatar);*/
        ImageLoaderManager.getInstance().showImage(avatar,finalUrl,getContext());


       convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicDetailActivity.launch(getContext(),bean);
            }
        });

    }
}
