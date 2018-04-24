package fr.dao.app.External.jgraph.inter;


import java.util.List;

import fr.dao.app.External.jgraph.models.Jchart;

/**
 * @author yun.
 * @date 2016/6/8
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */
public interface IChart {

    /**
     * 传入 数据
     */
    public void cmdFill(Jchart... jcharts);


    /**
     * 传入 数据
     */
    public void cmdFill(List<Jchart> jchartList);

}
