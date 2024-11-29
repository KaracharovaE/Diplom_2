package diplom2;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Order {
    private List<String> ingredients = new ArrayList<>();

    public void setIngredients(String[] ingredients) {
        this.ingredients = new ArrayList<>(List.of(ingredients));
    }
}

