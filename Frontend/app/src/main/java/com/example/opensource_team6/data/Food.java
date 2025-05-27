package com.example.opensource_team6.data;

public class Food {
    private String name;
    private String data_type;
    private String origin;
    private String category;
    private String rep_name;
    private double standard_amount;
    private double energy;
    private double protein;
    private double fat;
    private double carbohydrate;
    private double sugar;
    private double fiber;
    private double calcium;
    private double potassium;
    private double sodium;
    private double cholesterol;
    private double saturated_fat;
    private double trans_fat;
    private double weight; // 실제 섭취량 계산용

    // --- Getters and Setters ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getData_type() { return data_type; }
    public void setData_type(String data_type) { this.data_type = data_type; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRep_name() { return rep_name; }
    public void setRep_name(String rep_name) { this.rep_name = rep_name; }

    public double getStandard_amount() { return standard_amount; }
    public void setStandard_amount(double standard_amount) { this.standard_amount = standard_amount; }

    public double getEnergy() { return energy; }
    public void setEnergy(double energy) { this.energy = energy; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getCarbohydrate() { return carbohydrate; }
    public void setCarbohydrate(double carbohydrate) { this.carbohydrate = carbohydrate; }

    public double getSugar() { return sugar; }
    public void setSugar(double sugar) { this.sugar = sugar; }

    public double getFiber() { return fiber; }
    public void setFiber(double fiber) { this.fiber = fiber; }

    public double getCalcium() { return calcium; }
    public void setCalcium(double calcium) { this.calcium = calcium; }

    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }

    public double getSodium() { return sodium; }
    public void setSodium(double sodium) { this.sodium = sodium; }

    public double getCholesterol() { return cholesterol; }
    public void setCholesterol(double cholesterol) { this.cholesterol = cholesterol; }

    public double getSaturated_fat() { return saturated_fat; }
    public void setSaturated_fat(double saturated_fat) { this.saturated_fat = saturated_fat; }

    public double getTrans_fat() { return trans_fat; }
    public void setTrans_fat(double trans_fat) { this.trans_fat = trans_fat; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
