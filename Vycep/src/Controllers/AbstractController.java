package Controllers;

import cz.cvut.fit.klimaada.vycep.IStatusView;
import cz.cvut.fit.klimaada.vycep.Model;
import cz.cvut.fit.klimaada.vycep.rest.MyRestFacade;

public class AbstractController {

	protected Model model;
	protected IStatusView view;

	public AbstractController(Model model) {
		super();
		this.model = model;
	}
	
	public void setView(IStatusView view) {
		this.view = view;
	}
	
}
