package com.labelprintertest.android.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.labelprintertest.android.Models.TicketInfo;
import com.labelprintertest.android.R;

import java.util.ArrayList;

import static com.labelprintertest.android.Common.Common.cm;

public class TicketNumberInputDlg extends Dialog {

    private TicketNumClickListener listener;

    public TicketNumberInputDlg(@NonNull Context context, TicketInfo info, final TicketNumClickListener listener) {
        super(context);

        this.listener = listener;

        setContentView(R.layout.ticket_num_input);

        final EditText ticketNumTxt = findViewById(R.id.ticketNumber);
        ticketNumTxt.setText(String.valueOf(info.getNum()));
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(9);
        ticketNumTxt.setFilters(FilterArray);
        ticketNumTxt.setSelection(ticketNumTxt.getText().toString().length());

        ArrayList<Button> btnArr = new ArrayList<>();
        btnArr.add((Button) findViewById(R.id.key0));
        btnArr.add((Button) findViewById(R.id.key1));
        btnArr.add((Button) findViewById(R.id.key2));
        btnArr.add((Button) findViewById(R.id.key3));
        btnArr.add((Button) findViewById(R.id.key4));
        btnArr.add((Button) findViewById(R.id.key5));
        btnArr.add((Button) findViewById(R.id.key6));
        btnArr.add((Button) findViewById(R.id.key7));
        btnArr.add((Button) findViewById(R.id.key8));
        btnArr.add((Button) findViewById(R.id.key9));

        for (int i=0; i<btnArr.size(); i++) {
            Button button = btnArr.get(i);
            button.setTag(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ticketNumTxt.getText().toString().equals("0")) {
                        ticketNumTxt.setText(String.valueOf(v.getTag()));
                    }else {
                        ticketNumTxt.setText(ticketNumTxt.getText().toString() + String.valueOf(v.getTag()));
                    }
                    ticketNumTxt.setSelection(ticketNumTxt.getText().toString().length());
                }
            });
        }

        Button clearBtn = findViewById(R.id.btnClear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketNumTxt.setText("0");
            }
        });

        Button deleteBtn = findViewById(R.id.btnDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnDeleteBtnClicked();
                    dismiss();
                }
            }
        });

        Button cofirmBtn = findViewById(R.id.btnConfirm);
        cofirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int ticketNum = cm.parseInteger(ticketNumTxt.getText().toString());
                    if (ticketNum > 0) {
                        listener.OnConfirmBtnClicked(ticketNum);
                        dismiss();
                    }else {
                        listener.OnDeleteBtnClicked();
                        dismiss();
                    }
                }
            }
        });
    }

    public TicketNumberInputDlg(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TicketNumberInputDlg(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface TicketNumClickListener {
        public abstract void OnDeleteBtnClicked ();
        public abstract void OnConfirmBtnClicked (int num);
    }
}
