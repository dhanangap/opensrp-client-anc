package org.smartregister.anc.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.LastContactDetailsWrapper;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.util.Utils;

import java.util.List;

public class LastContactDetailsAdapter extends RecyclerView.Adapter<LastContactDetailsAdapter.ViewHolder> {


    private List<LastContactDetailsWrapper> lastContactDetailsTestsList;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public LastContactDetailsAdapter(Context context, List<LastContactDetailsWrapper> lastContactDetailsTestsList) {
        this.mInflater = LayoutInflater.from(context);
        this.lastContactDetailsTestsList = lastContactDetailsTestsList;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.previous_contacts_preview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (lastContactDetailsTestsList.size() > 0) {

            LastContactDetailsWrapper lastContactDetailsTest = lastContactDetailsTestsList.get(position);
            Facts facts = lastContactDetailsTest.getFacts();

            createContactDetailsView(lastContactDetailsTest.getExtraInformation(), facts, holder);
        }
    }

    private void createContactDetailsView(List<YamlConfigWrapper> data, Facts facts,
                                          LastContactDetailsAdapter.ViewHolder holder) {
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getYamlConfigItem() != null) {

                    YamlConfigItem yamlConfigItem = data.get(i).getYamlConfigItem();

                    Template template = getTemplate(yamlConfigItem.getTemplate());
                    String output = "";
                    if (!TextUtils.isEmpty(template.detail)) {
                        output = Utils.fillTemplate(template.detail, facts);
                    }

                    holder.sectionDetailTitle.setText(template.title);
                    holder.sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation

                    if (AncApplication.getInstance().getAncRulesEngineHelper()
                            .getRelevance(facts, yamlConfigItem.getIsRedFont())) {
                        holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                        holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                    } else {
                        holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_left));
                        holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_right));


                    }

                    holder.sectionDetailTitle.setVisibility(View.VISIBLE);
                    holder.sectionDetails.setVisibility(View.VISIBLE);
                }
            }
        }


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return lastContactDetailsTestsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionDetails;
        TextView sectionDetailTitle;
        public View parent;

        ViewHolder(View itemView) {
            super(itemView);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);
            parent = itemView;
        }
    }

    private Template getTemplate(String rawTemplate) {
        Template template = new Template();

        if (rawTemplate.contains(":")) {
            String[] templateArray = rawTemplate.split(":");
            if (templateArray.length == 1) {
                template.title = templateArray[0].trim();
            } else if (templateArray.length > 1) {
                template.title = templateArray[0].trim();
                template.detail = templateArray[1].trim();
            }
        } else {
            template.title = rawTemplate;
            template.detail = "true";
        }

        return template;

    }

    private class Template {
        public String title = "";
        public String detail = "";
    }

}