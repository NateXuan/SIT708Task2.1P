package com.example.sit708_task21p;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerSourceUnit;
    private Spinner spinnerDestUnit;
    private EditText editTextValue;
    private Button buttonConvert;
    private TextView textViewResult;
    private String selectedConversionType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerSourceUnit = findViewById(R.id.spinner_source_unit);
        spinnerDestUnit = findViewById(R.id.spinner_dest_unit);
        editTextValue = findViewById(R.id.editText_value);
        buttonConvert = findViewById(R.id.button_convert);
        textViewResult = findViewById(R.id.textView_result);
        Spinner spinnerConversionType = findViewById(R.id.spinner_conversion_type);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.conversion_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConversionType.setAdapter(typeAdapter);

        spinnerConversionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedConversionType = parent.getItemAtPosition(position).toString().toLowerCase();
                updateUnitSpinnersBasedOnType(selectedConversionType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performConversion();
            }
        });
    }

    private void setupSpinners(String type) {
        ArrayAdapter<CharSequence> adapter;
        switch (type) {
            case "length":
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.length_units, android.R.layout.simple_spinner_item);
                break;
            case "weight":
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.weight_units, android.R.layout.simple_spinner_item);
                break;
            case "temperature":
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.temperature_units, android.R.layout.simple_spinner_item);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSourceUnit.setAdapter(adapter);
        spinnerDestUnit.setAdapter(adapter);
    }

    private void updateUnitSpinnersBasedOnType(String type) {
        setupSpinners(type);
    }

    private void performConversion() {
        try {
            String sourceUnit = spinnerSourceUnit.getSelectedItem().toString();
            String destUnit = spinnerDestUnit.getSelectedItem().toString();
            double inputValue = Double.parseDouble(editTextValue.getText().toString());
            double convertedValue = convertUnits(sourceUnit, destUnit, inputValue);
            textViewResult.setText(String.format("%.2f %s", convertedValue, destUnit));
        } catch (NumberFormatException e) {
            textViewResult.setText("Invalid input");
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            textViewResult.setText("Invalid conversion");
            Toast.makeText(this, "Invalid conversion", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertUnits(String sourceUnit, String destUnit, double inputValue) {
        switch (selectedConversionType) {
            case "length": {
                double lengthBaseValue; // Convert to cm as base
                switch (sourceUnit) {
                    case "Inch":
                        lengthBaseValue = inputValue * 2.54;
                        break;
                    case "Foot":
                        lengthBaseValue = inputValue * 30.48;
                        break;
                    case "Yard":
                        lengthBaseValue = inputValue * 91.44;
                        break;
                    case "Mile":
                        lengthBaseValue = inputValue * 160934;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid source unit");
                }
                switch (destUnit) {
                    case "Inch":
                        return lengthBaseValue / 2.54;
                    case "Foot":
                        return lengthBaseValue / 30.48;
                    case "Yard":
                        return lengthBaseValue / 91.44;
                    case "Mile":
                        return lengthBaseValue / 160934;
                    default:
                        throw new IllegalArgumentException("Invalid destination unit");
                }
            }

            case "weight": {
                double weightBaseValue; // Convert to kg as base
                switch (sourceUnit) {
                    case "Pound":
                        weightBaseValue = inputValue * 0.453592;
                        break;
                    case "Ounce":
                        weightBaseValue = inputValue * 0.0283495;
                        break;
                    case "Ton":
                        weightBaseValue = inputValue * 907.185;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid source unit for weight");
                }
                switch (destUnit) {
                    case "Pound":
                        return weightBaseValue / 0.453592;
                    case "Ounce":
                        return weightBaseValue / 0.0283495;
                    case "Ton":
                        return weightBaseValue / 907.185;
                    default:
                        throw new IllegalArgumentException("Invalid destination unit for weight");
                }
            }

            case "temperature": {
                if (sourceUnit.equals("Celsius") && destUnit.equals("Fahrenheit")) {
                    return (inputValue * 9 / 5) + 32;
                } else if (sourceUnit.equals("Fahrenheit") && destUnit.equals("Celsius")) {
                    return (inputValue - 32) * 5 / 9;
                } else if (sourceUnit.equals("Celsius") && destUnit.equals("Kelvin")) {
                    return inputValue + 273.15;
                } else if (sourceUnit.equals("Kelvin") && destUnit.equals("Celsius")) {
                    return inputValue - 273.15;
                } else if (sourceUnit.equals("Fahrenheit") && destUnit.equals("Kelvin")) {
                    return (inputValue - 32) * 5 / 9 + 273.15;
                } else if (sourceUnit.equals("Kelvin") && destUnit.equals("Fahrenheit")) {
                    return (inputValue - 273.15) * 9 / 5 + 32;
                } else {
                    throw new IllegalArgumentException("Invalid temperature conversion");
                }
            }

            default:
                throw new IllegalArgumentException("Invalid conversion type");
        }
    }
}
