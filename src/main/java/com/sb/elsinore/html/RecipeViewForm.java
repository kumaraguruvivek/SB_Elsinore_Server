package com.sb.elsinore.html;

import ca.strangebrew.recipe.Hop;
import ca.strangebrew.recipe.Mash;
import ca.strangebrew.recipe.Recipe;
import com.sb.common.SBStringUtils;
import com.sb.elsinore.Messages;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import java.io.IOException;

import static com.sb.elsinore.triggers.TemperatureTrigger.TEMPPROBE;
import static org.rendersnake.HtmlAttributesFactory.*;

/**
 * Render a specific recipe in HTML for use.
 * Created by Doug Edey on 24/02/15.
 */
public class RecipeViewForm implements Renderable {
    private Recipe currentRecipe = null;

    public RecipeViewForm(Recipe newRecipe) {
        this.currentRecipe = newRecipe;
    }

    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
        if (this.currentRecipe == null) {
            System.out.println("No recipe defined. Stacktrace for call: ");
            Exception runtimeException = new RuntimeException();
            runtimeException.printStackTrace();
            return;
        }

        html.macros().stylesheet("/bootstrap-v4/css/bootstrap.min.css");

        html.div(id("recipeView").class_("text-center"));
        renderMash(html);
        renderHops(html);
        renderFermentation(html);
        renderDryHops(html);
        html._div();
    }

    public HtmlCanvas renderDryHops(HtmlCanvas html) throws IOException {
        // Do we have hop additions?
        if (this.currentRecipe.getHopsListSize() > 0) {
            // Show them!
            html.div(id("recipeDryHops"));
            html.div(class_("lead")).write(Messages.DRY_ADDITIONS);
            html._div();
            html.table(id("hopTable").class_("table table-striped table-bordered"));

            html.tr(id("hopTitle"));
            html.th().write(Messages.HOP)._th();
            html.th().write(Messages.AMOUNT)._th();
            html.th().write(Messages.ALPHA)._th();
            html.th().write(Messages.TIME)._th();
            html._tr();

            for (int i = 0; i < this.currentRecipe.getHopsListSize(); i++) {
                Hop currentHop = this.currentRecipe.getHop(i);
                if (!currentHop.getAdd().equals(Hop.DRY)) {
                    continue;
                }
                html.tr(id("hopDry-" + i));
                html.td(id("hop-Name")).write(currentHop.getName())._td();
                html.td(id("hop-Amount")).write(currentHop.getAmount().toString())._td();
                html.td(id("hop-Alpha")).write(String.format("%.2f", currentHop.getAlpha()))._td();
                html.td(id("hop-Time")).write(SBStringUtils.formatTime(currentHop.getMinutes()))._td();
                html._tr();
            }
            html._table();
            html.select(name(TEMPPROBE).class_("form-control"));
            html.option(value("").selected_if(true))
                    .write("Select Probe")
                    ._option();
//            for (Device device: deviceRepository.get) {
//                html.option(value(entry.getName()))
//                        .write(entry.getName())
//                        ._option();
//            }
            html._select();
            html.button(id("setDryHopProfile").class_("btn btn-default").onClick("setDryHopProfile(this)"))
                    .write(Messages.SET_DRY_HOP_PROFILE)._button();
            html._div();
        }
        return html;
    }

    public HtmlCanvas renderFermentation(HtmlCanvas html) throws IOException {
        if (this.currentRecipe.getFermentStepSize() > 0) {
            html.div(id("recipeFermProfile"));
            html.div(class_("lead")).write(Messages.FERMENT_PROFILE);
            html._div();
            html.table(id("fermentTable").class_("table table-striped table-bordered"));
            html.tr();
            html.th().write(Messages.NAME)._th();
            html.th().write(Messages.TEMP)._th();
            html.th().write(Messages.TIME)._th();
            html._tr();

            for (int i = 0; i < this.currentRecipe.getFermentStepSize(); i++) {
                html.tr(id("fermStep-" + i));
                html.td(id("ferm-Name")).write(this.currentRecipe.getFermentStepType(i))._td();
                html.td(id("ferm-Temp")).write(String.format("%.2f", this.currentRecipe.getFermentStepTemp(i)) + this.currentRecipe.getFermentStepTempU(i))._td();
                html.td(id("ferm-Time")).write(this.currentRecipe.getFermentStepTime(i) + " days")._td();
                html._tr();
            }
            html._table();
            html.select(name(TEMPPROBE).class_("form-control"));
            html.option(value("").selected_if(true))
                    .write("Select Probe")
                    ._option();
//            for (Temp entry : LaunchControl.getInstance().tempList) {
//                if (entry instanceof PID) {
//                    html.option(value(entry.getName()))
//                            .write(entry.getName())
//                            ._option();
//                }
//            }
            html._select();
            html.button(id("setFermProfile").class_("btn btn-default").onClick("setFermProfile(this)"))
                    .write(Messages.SET_FERM_PROFILE)._button();
            html._div();
        }
        return html;
    }

    /****
     * Render the hops values for a HTML form
     * @param html The HTML Canvas to add the values to
     * @return The Updated HTML Canvas
     * @throws IOException If we can't add the info
     */

    public HtmlCanvas renderHops(HtmlCanvas html) throws IOException {
        // Do we have hop additions?
        if (this.currentRecipe.getHopsListSize() > 0) {
            // Show them!
            html.div(id("recipeBoilHops"));
            html.div(class_("lead")).write(Messages.BOIL_ADDITIONS);
            html._div();
            html.table(id("hopTable").class_("table table-striped table-bordered"));
            html.tr(id("hopTitle"));
            html.th().write(Messages.HOP)._th();
            html.th().write(Messages.AMOUNT)._th();
            html.th().write(Messages.IBU)._th();
            html.th().write(Messages.ALPHA)._th();
            html.th().write(Messages.TIME)._th();
            html._tr();

            for (int i = 0; i < this.currentRecipe.getHopsListSize(); i++) {
                if (!this.currentRecipe.getHop(i).getAdd().equals(Hop.BOIL)) {
                    continue;
                }
                html.tr(id("hopAdd-" + i));
                html.td(id("hop-Name")).write(this.currentRecipe.getHop(i).getName())._td();
                html.td(id("hop-Amount")).write(this.currentRecipe.getHop(i).getAmount().toString())._td();
                html.td(id("hop-IBU")).write(String.format("%.2f", this.currentRecipe.getHop(i).getIBU()))._td();
                html.td(id("hop-Alpha")).write(String.format("%.2f", this.currentRecipe.getHop(i).getAlpha()))._td();
                html.td(id("hop-Time")).write(SBStringUtils.formatTime(this.currentRecipe.getHop(i).getMinutes()))._td();
                html._tr();
            }
            html._table();
            html.select(name(TEMPPROBE).class_("form-control"));
            html.option(value("").selected_if(true))
                    .write("Select Probe")
                    ._option();
//            for (Temp entry : LaunchControl.getInstance().tempList) {
//                html.option(value(entry.getName()))
//                        .write(entry.getName())
//                        ._option();
//            }
            html._select();
            html.button(id("setBoilHopProfile").class_("btn btn-default").onClick("setBoilHopProfile(this)"))
                    .write(Messages.SET_BOIL_HOP_PROFILE)._button();
            html._div();
        }
        return html;
    }

    public HtmlCanvas renderMash(HtmlCanvas html) throws IOException {
        // Do we have mash steps?
        if (this.currentRecipe.getMash() != null && this.currentRecipe.getMash().getStepSize() > 0) {
            // Show them!
            Mash mash = this.currentRecipe.getMash();
            html.div(id("recipeMash"));
            html.div(class_("lead")).write(Messages.MASH_PROFILE);
            html._div();
            html.table(id("mashTable").class_("table table-striped table-bordered"));

            html.tr();
            html.td().write(Messages.METHOD)._td();
            html.td().write(Messages.TYPE)._td();
            html.td().write(Messages.START)._td();
            html.td().write(Messages.END_TEMP)._td();
            html.td().write(Messages.TIME)._td();
            html._tr();

            for (int i = 0; i < mash.getStepSize(); i++) {
                html.tr(id("mashStep-" + i));
                html.td(id("step-Method")).write(mash.getStepMethod(i))._td();
                html.td(id("step-Type")).write(mash.getStepType(i))._td();
                html.td(id("step-startTemp")).write(String.format("%.2f", mash.getStepStartTemp(i)) + mash.getStepTempU(i))._td();
                html.td(id("step-endTemp")).write(String.format("%.2f", mash.getStepEndTemp(i)) + mash.getStepTempU(i))._td();
                html.td(id("step-time")).write(SBStringUtils.formatTime(mash.getStepMin(i)))._td();
                html._tr();
            }
            html._table();
            html.select(name(TEMPPROBE).class_("form-control"));
            html.option(value("").selected_if(true))
                    .write("Select Probe")
                    ._option();
//            for (Temp entry : LaunchControl.getInstance().tempList) {
//                if (entry instanceof PID) {
//                    html.option(value(entry.getName()))
//                            .write(entry.getName())
//                            ._option();
//                }
//            }
            html._select();
            html.button(id("setMashProfile").class_("btn btn-default").onClick("setMashProfile(this)"))
                    .write(Messages.SET_MASH_PROFILE)._button();
            html._div();
        }
        return html;
    }
}
