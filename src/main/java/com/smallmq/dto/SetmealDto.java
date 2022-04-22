package com.smallmq.dto;


import com.smallmq.pojo.Setmeal;
import com.smallmq.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
