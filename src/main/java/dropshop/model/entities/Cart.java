package dropshop.model.entities;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Cart {
    private Map<Integer, Integer> items = new HashMap<>();


    public Cart(){}


    public void addItem(int productId, int quantity){
        if(quantity <= 0){
            throw new IllegalArgumentException("Quantity must be at least 1");
        }else{
            items.put(productId, items.getOrDefault(productId, 0) + quantity);
        }
    }
    public void removeoneItem(int productId){
        if(items.containsKey(productId)){
            int currentQuantity = items.get(productId);
            if(currentQuantity > 1){
                items.put(productId, currentQuantity - 1);
            }else{
                items.remove(productId);
            }
        }
    }
    public void removewholeItem(int prodcutId){
        items.remove(prodcutId);
    }

    public Map<Integer, Integer> getItems(){
        return Map.copyOf(items);
    }
    public void clearCart(){
        items.clear();
    }

}
  