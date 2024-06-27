var express = require('express');
var app = express();

const {Organization} = require('./DbConfig.js');
const {Fund} = require('./DbConfig.js');
const {Contributor} = require('./DbConfig.js');
const {Donation} = require('./DbConfig.js');


/*
Return an org with login specified as req.query.login and password specified as 
req.query.password; this essentially acts as login for organizations
*/
app.use('/findOrgByLoginAndPassword', (req, res) => {

	var query = {"login" : req.query.login, "password" : req.query.password };
    
	Organization.findOne( query, (err, result) => {
		if (err) {
		    res.json({ "status": "error", "data" : err});
		}
		else {
		    //console.log(result);
		    res.json({ "status" : "success", "data" : result});
		}
	    });
    });

/*
 Check if an organization login username is already taken
*/

app.use('/checkUsername', (req, res) => {
	var query = {"login":req.query.login};
	
	Organization.findOne(query, (err, result) =>{
		if (err) {
			res.json({"status" : "error", "data" : err});
		}
		else if (result) {
			res.json({"status" : "taken"});
		} else {
			res.json({"status" : "available"});
		}
	})
})

/*
 Create a new organiztion
*/
app.use('/createOrg', (req, res) => {
	var organization = new Organization({
		login: req.query.login,
		password: req.query.password,
		name: req.query.name,
		description: req.query.description,
		funds: []
	});
	
	organization.save((err, newOrg) => {
		if (err) {
			res.json({"status" : "error", "data" : err});
		}
		else {
			res.json({"status" : "success", "data" : newOrg});
		}
	});

});

app.use('/changePassword', (req, res) => {
	var filter = {"_id" : req.query.id };
	var update = {"password" : req.query.password};
	var action = {"$set" : update};
	
	Organization.findOneAndUpdate (filter, action, {new : true}, (err, result) =>
	{
		if (err) {
			res.json({"status" : "error", "data" : err});
		}
		else if (!result) {
			res.json({"status" : "change failed"});
		}
		else {
			res.json({"status" : "success", "data" : result});
		}
		
	});
});

/*
Create a new fund
*/
app.use('/createFund', (req, res) => {

	var fund = new Fund({
		name: req.query.name,
		description: req.query.description,
		target: req.query.target,
		org: req.query.orgId,
		donations: []
	    });

	fund.save( (err) => {
		if (err) {
		    res.json({ "status": "error", "data" : err});
		}
		else {
		    //console.log(fund);

		    /*
		      In addition to updating the Fund collection, we also need to
		      update the Organization object to which this Fund belongs.
		    */

		    var filter = {"_id" : req.query.orgId };

		    var action = { "$push" : { "funds" : fund } };

		    Organization.findOneAndUpdate( filter, action, { "new" : true },  (err, result) => {
			    //console.log(result);
			    if (err) {
				res.json({ "status": "error", "data" : err});
			    }
			    else {
				res.json({ "status": "success", "data" : fund});
			    }
			});
		
		}
	    });

    });

/*
Return the Fund with ID specified as req.query.id
*/
app.use('/findFundById', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Fund.findOne( query, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else {
		    //console.log(result);
		    res.json({'status' : 'success', 'data' : result});
		}
	    });
	
    });

/*
Delete the fund with ID specified as req.query.id
*/
app.use('/deleteFund', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Fund.findOneAndDelete( query, (err, orig) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else {
		    //console.log(orig);

		    /*
		      In addition to removing this from the Fund collection,
		      we also need to remove it from the Organization to which it belonged.
		    */

		    var filter = {"_id" : orig.org };
		    var action = { "$pull" : { "funds" : { "_id" : req.query.id } } };

		    Organization.findOneAndUpdate(filter, action, (err) => {
			    if (err) {
				res.json({'status': 'error', 'data' : err});
			    }
			    else {
				res.json({'status': 'success', 'data': orig});
			    }
			});
		}
	    });

    });



/*
Return the name of the contributor with ID specified as req.query.id
*/
app.use('/findContributorNameById', (req, res) => {
    
	var query = { "_id" : req.query.id };
	
	Contributor.findOne(query, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else if (!result) {
		    res.json({'status': 'not found'});
		}
		else {
		    res.json({'status': 'success', 'data': result.name});
		}
		
	    });
    });


/*
Make a new donation to the fund with ID specified as req.query.fund
from the contributor with ID specified as req.query.contributor
*/
app.use('/makeDonation', (req, res) => {

	var donation = new Donation({
		contributor: req.query.contributor,
		fund: req.query.fund,
		date: Date.now(),
		amount: req.query.amount
	    });


	donation.save( (err) => {
		if (err) {
		    res.json({'status' : 'error', 'data' : err});
		}
		else {
		    //console.log(donation);

		    /*
		      In addition to creating a new Donation object, we need to update
		      the Contributor's array of donations
		    */

		    var filter = { "_id" : req.query.contributor };
		    var action = { "$push" : { "donations" : donation } };

		    Contributor.findOneAndUpdate(filter, action, (err) => {

			    if (err) {
				res.json({'status' : 'error', 'data' : err});
			    }
			    else {
				
				var filter = { "_id" : req.query.fund };
				var action = { "$push" : { "donations" : donation } };

				/*
				  We also need to update the Fund's array of donations
				*/
				Fund.findOneAndUpdate(filter, action, { "new" : true }, (err, fund) => {

					if (err) {
					    res.json({'status' : 'error', 'data' : err});
					}
					else {

					    var query = { "_id" : fund.org };

					    var whichFund = { "funds" : { "_id" : { "$in" : [fund._id] }  } }; 

					    var action = { "$pull" : whichFund };

					    /*
					      We also need to update the Fund in the Organization.
					      To do this, I remove the Fund and then replace it with the updated one.
					    */

					    Organization.findOneAndUpdate(query, action, (err, org) => {


						    if (err) {
							res.json({'status' : 'error', 'data' : err});
						    }
						    else {    

							var query = { "_id" : fund.org };
					    
							var action = { "$push" : { "funds" : fund } };
							
							Organization.findOneAndUpdate(query, action, { "new" : true }, (err, org) => {
								
								if (err) {
								    res.json({'status' : 'error', 'data' : err});
								}
								else {
								    //console.log(org);
								    res.json({'status' : 'success', 'data' : donation });
								}


							    });


						    }

						});

					}
				    });
			    }


			});

		}
	    });

    });


    

/*
Return the contributor with login specified as req.query.login and password specified
as req.query.password; essentially acts as login for contributors.
*/
app.use('/findContributorByLoginAndPassword', (req, res) => {

	var query = {"login" : req.query.login, "password" : req.query.password };
    
	Contributor.findOne( query, (err, result) => {
		if (err) {
		    res.json({ "status": "error", "data" : err});
		}
		else if (!result){
		    res.json({ "status": "login failed" });
		}
		else {
		    //console.log(result);
		    res.json({ "status" : "success", "data" : result});
		}
	    });
	

    });


/*
Return the name of the fund with ID specified as req.query.id
*/
app.use('/findFundNameById', (req, res) => {
    
	var query = { "_id" : req.query.id };
	
	Fund.findOne(query, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else if (!result) {
		    res.json({'status': 'not found'});
		}
		else {
		    res.json({'status': 'success', 'data': result.name});
		}
		
	    });
    });



/*
Return information about all organizations, so that user can choose one to contribute to.
Rather than return all info in database, this just returns the org's ID, name, and funds.
For each fund, it only includes the ID, name, target, and total donations so far.
*/
app.use('/allOrgs', (req, res) => {

	Organization.find({}, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else {

		    var organizations = [];

		    result.forEach( (org) => {
			    
			    var funds = [];

			    org.funds.forEach( (fund) => {

				    var totalDonations = 0;

				    fund.donations.forEach( (donation) => {
					    totalDonations += donation.amount;
					});

				    var fundResult = {
					'_id' : fund._id,
					'name' : fund.name,
					'target' : fund.target,
					'totalDonations' : totalDonations
				    };

				    funds.push(fundResult);

				});

			    var orgResult = {
				'_id' : org._id,
				'name' : org.name,
				'funds' : funds
			    };

			    organizations.push(orgResult);

			});

		    //console.log(organizations);
		    res.json({'status' : 'success', 'data': organizations});
		}
	    }).sort({ 'name': 'asc' });
    });



/********************************************************/


app.listen(3001,  () => {
	console.log('Listening on port 3001');
    });
