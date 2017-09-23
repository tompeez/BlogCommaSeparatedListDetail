package view.beans;

import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;


public class EmpBean {
    public EmpBean() {
        super();
    }
    public String getEmpNameById() {
        String ret = "Not Found!";
        // GET A METHOD FROM PAGEDEF AND EXECUTE IT
        // get the binding container
        BindingContainer bindings =
            BindingContext.getCurrent().getCurrentBindingsEntry();

        // get an Action or MethodAction
        OperationBinding method =
            bindings.getOperationBinding("ExecuteWithParams");

        if (method == null) {
            return ret;
            // handle method not found error...
        }

        String val = evaluateEx("#{emp}");
        // if there are parameters to set...
        Map paramsMap = method.getParamsMap();
        paramsMap.put("bindId", val);
        // execute the method
        method.execute();
        List errors = method.getErrors();
        if (!errors.isEmpty()) {
            // handle errors here errors is a list of exceptions!
            ((Exception)(errors.get(0))).printStackTrace();
        } else {
            // Get the data from an ADF tree or table
            DCBindingContainer dcBindings = (DCBindingContainer)bindings;

            // Get a attribute value of the current row of iterator
            DCIteratorBinding iterBind =
                (DCIteratorBinding)dcBindings.get("EmpLookupByIdIterator");
            ret = (String)iterBind.getCurrentRow().getAttribute("Name");
        }

        return ret;
    }

    private String evaluateEx(String ex) {
        // Get the current row of a iterator , a different way
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
        ValueExpression ve =
            ef.createValueExpression(ctx.getELContext(), ex, String.class);
        String test = (String)ve.getValue(ctx.getELContext());
        return test;
    }
}
