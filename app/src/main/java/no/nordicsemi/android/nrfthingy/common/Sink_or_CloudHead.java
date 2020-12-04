package no.nordicsemi.android.nrfthingy.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import no.nordicsemi.android.nrfthingy.R;
import no.nordicsemi.android.nrfthingy.WelcomeActivity;
import no.nordicsemi.android.nrfthingy.configuration.InitialConfigurationActivity;

public class Sink_or_CloudHead extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sink_or__cloud_head);
        //sink button
        Button button_sink=findViewById(R.id.sink);
        //cloud head
        Button button_clouster_head=findViewById(R.id.could_head);


        //Go to clould head
        button_clouster_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //New intent for sink
                Intent intent = new Intent(Sink_or_CloudHead.this, Clouster_Head_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

    }
}